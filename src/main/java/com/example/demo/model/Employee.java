package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String designation; // e.g., HOD, Clerk, Lab Assistant

    @Column(name = "employee_type", nullable = false)
    private String employeeType; // e.g., TEACHING, NON_TEACHING, ADMIN

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
