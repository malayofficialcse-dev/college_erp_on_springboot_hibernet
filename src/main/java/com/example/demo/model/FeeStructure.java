package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "fee_structures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @NotNull
    @Column(name = "semester_number", nullable = false)
    private Integer semesterNumber;

    @Column(name = "tuition_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal tuitionFee;

    @Column(name = "exam_fee", precision = 12, scale = 2)
    private BigDecimal examFee;

    @Column(name = "library_fee", precision = 12, scale = 2)
    private BigDecimal libraryFee;

    @Column(name = "hostel_fee", precision = 12, scale = 2)
    private BigDecimal hostelFee;

    @Column(name = "transport_fee", precision = 12, scale = 2)
    private BigDecimal transportFee;

    @Column(name = "other_fee", precision = 12, scale = 2)
    private BigDecimal otherFee;
}
