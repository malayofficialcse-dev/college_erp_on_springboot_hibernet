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
@Table(name = "counseling")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE counseling SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Counseling extends AuditableSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email
    @NotBlank
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "previous_qualification")
    private String previousQualification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desired_course_id")
    private Course desiredCourse;

    @Column(name = "counselor_name")
    private String counselorName;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    // Status: PENDING, ADMITTED, REJECTED
    @Column(name = "status")
    private String status = "PENDING";
}
