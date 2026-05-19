package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "exam_name", nullable = false)
    private String examName; // e.g., "Mid-Term Exam", "End-Semester Exam"

    @Column(name = "exam_type")
    private String examType; // MIDTERM, ENDSEM, PRACTICAL, INTERNAL, VIVA

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_marks")
    private Double totalMarks;

    @Column(name = "passing_marks")
    private Double passingMarks;

    @Column(name = "status")
    private String status = "SCHEDULED"; // SCHEDULED, ONGOING, COMPLETED, CANCELLED
}
