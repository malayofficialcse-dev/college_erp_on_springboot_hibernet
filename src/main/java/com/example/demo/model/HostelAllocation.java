package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "hostel_allocations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostelAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private HostelRoom hostelRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id")
    private AcademicYear academicYear;

    @NotNull
    @Column(name = "allotment_date", nullable = false)
    private LocalDate allotmentDate;

    @Column(name = "vacating_date")
    private LocalDate vacatingDate;

    @Column(name = "status")
    private String status = "ACTIVE"; // ACTIVE, VACATED, CANCELLED
}
