package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "academic_years")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "year_label", nullable = false, unique = true)
    private String yearLabel; // e.g., "2025-2026"

    @NotNull
    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @NotNull
    @Column(name = "end_year", nullable = false)
    private Integer endYear;

    @Column(name = "is_current")
    private boolean isCurrent = false;
}
