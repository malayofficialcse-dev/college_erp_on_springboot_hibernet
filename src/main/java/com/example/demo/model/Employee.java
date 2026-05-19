package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE employees SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Employee extends AuditableSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(nullable = false)
    private String designation; // e.g., HOD, Clerk, Lab Assistant

    @Column(name = "employee_type", nullable = false)
    private String employeeType; // TEACHING, NON_TEACHING, ADMIN

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "basic_salary", precision = 12, scale = 2)
    private java.math.BigDecimal basicSalary;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "status")
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, RESIGNED, RETIRED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
