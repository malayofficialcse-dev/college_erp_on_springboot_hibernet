package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE courses SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Course extends AuditableSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "course_code", nullable = false, unique = true)
    private String courseCode; // e.g., "CS101"

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_semesters")
    private Integer totalSemesters; // e.g., 8 for B.Tech

    @Column(name = "duration_years")
    private Integer durationYears;

    private Integer credits;

    @Column(name = "course_type")
    private String courseType; // UNDERGRADUATE, POSTGRADUATE, DIPLOMA, PhD

    @Column(name = "status")
    private String status = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
