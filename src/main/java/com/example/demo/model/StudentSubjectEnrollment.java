package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(
        name = "student_subject_enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "subject_id", "semester_number"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE student_subject_enrollments SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class StudentSubjectEnrollment extends AuditableSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "semester_number", nullable = false)
    private Integer semesterNumber;

    @Column(name = "enrollment_type", nullable = false)
    private String enrollmentType = "REGULAR";

    @Column(name = "status", nullable = false)
    private String status = "ENROLLED";

    @Column(name = "enrolled_on", nullable = false)
    private LocalDate enrolledOn = LocalDate.now();
}
