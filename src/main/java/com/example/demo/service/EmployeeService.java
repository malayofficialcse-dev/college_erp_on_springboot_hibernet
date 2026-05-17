package com.example.demo.service;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee existing = employee.get();
            existing.setFirstName(employeeDetails.getFirstName());
            existing.setLastName(employeeDetails.getLastName());
            existing.setEmail(employeeDetails.getEmail());
            existing.setDesignation(employeeDetails.getDesignation());
            existing.setEmployeeType(employeeDetails.getEmployeeType());
            existing.setDepartment(employeeDetails.getDepartment());
            return employeeRepository.save(existing);
        }
        return null;
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
