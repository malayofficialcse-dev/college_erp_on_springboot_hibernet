package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    @Autowired private EmployeeRepository employeeRepository;

    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    public Page<Employee> getByDepartment(Long deptId, Pageable pageable) {
        return employeeRepository.findByDepartmentId(deptId, pageable);
    }

    public Page<Employee> getByType(String type, Pageable pageable) {
        return employeeRepository.findByEmployeeType(type, pageable);
    }

    public Page<Employee> getByStatus(String status, Pageable pageable) {
        return employeeRepository.findByStatus(status, pageable);
    }

    public Page<Employee> search(Long departmentId, String employeeType, String status, String keyword, Pageable pageable) {
        return employeeRepository.search(departmentId, emptyToNull(employeeType), emptyToNull(status), emptyToNull(keyword), pageable);
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + employee.getEmail());
        }
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee details) {
        Employee emp = getEmployeeById(id);
        emp.setFirstName(details.getFirstName());
        emp.setLastName(details.getLastName());
        emp.setPhone(details.getPhone());
        emp.setDesignation(details.getDesignation());
        emp.setEmployeeType(details.getEmployeeType());
        emp.setAddress(details.getAddress());
        emp.setBasicSalary(details.getBasicSalary());
        emp.setStatus(details.getStatus());
        emp.setDepartment(details.getDepartment());
        return employeeRepository.save(emp);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        getEmployeeById(id);
        employeeRepository.deleteById(id);
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
