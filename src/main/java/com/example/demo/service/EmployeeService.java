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
    @Autowired private com.example.demo.repository.UserRepository userRepository;
    @Autowired private UserService userService;
    @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

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
        String officialEmail = employee.getEmployeeCode().toLowerCase() + "@college.edu";
        employee.setEmail(officialEmail);

        if (employeeRepository.findByEmail(officialEmail).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + officialEmail);
        }
        if (userRepository.existsByUsername(employee.getEmployeeCode())) {
            throw new IllegalArgumentException("Username already exists: " + employee.getEmployeeCode());
        }

        Employee savedEmployee = employeeRepository.save(employee);

        // Auto-create User account for this employee
        com.example.demo.model.User user = new com.example.demo.model.User();
        user.setUsername(savedEmployee.getEmployeeCode());
        user.setEmail(officialEmail);
        user.setPassword(passwordEncoder.encode("Welcome@123")); // Default password
        user.setEnabled(true);
        user.setEmployeeId(savedEmployee.getId());
        user.setEmployeeCode(savedEmployee.getEmployeeCode());
        user.setFullName(savedEmployee.getFirstName() + " " + savedEmployee.getLastName());

        java.util.Set<com.example.demo.model.ERole> roles = new java.util.HashSet<>();
        String type = savedEmployee.getEmployeeType() != null ? savedEmployee.getEmployeeType().toUpperCase() : "";
        String designation = savedEmployee.getDesignation() != null ? savedEmployee.getDesignation().toUpperCase() : "";
        
        if (designation.contains("HOD")) {
            roles.add(com.example.demo.model.ERole.ROLE_HOD);
            roles.add(com.example.demo.model.ERole.ROLE_TEACHER);
        } else if (designation.contains("LIBRARIAN")) {
            roles.add(com.example.demo.model.ERole.ROLE_LIBRARIAN);
        } else if (designation.contains("ACCOUNTANT")) {
            roles.add(com.example.demo.model.ERole.ROLE_ACCOUNTANT);
        } else if (designation.contains("WARDEN")) {
            roles.add(com.example.demo.model.ERole.ROLE_HOSTEL_WARDEN);
        } else if (designation.contains("PRINCIPAL")) {
            roles.add(com.example.demo.model.ERole.ROLE_PRINCIPAL);
        } else if ("TEACHING".equals(type) || designation.contains("TEACHER") || designation.contains("PROFESSOR")) {
            roles.add(com.example.demo.model.ERole.ROLE_TEACHER);
        } else if ("ADMIN".equals(type)) {
            roles.add(com.example.demo.model.ERole.ROLE_ADMIN);
        } else {
            roles.add(com.example.demo.model.ERole.ROLE_STAFF);
        }
        user.setRoles(roles);

        com.example.demo.model.User savedUser = userRepository.save(user);

        // Initialize User permissions for all system modules (defaulting to false)
        userService.initUserPermissions(savedUser);

        return savedEmployee;
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
