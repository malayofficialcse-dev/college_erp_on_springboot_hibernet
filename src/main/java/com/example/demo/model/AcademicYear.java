package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "academic_years")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE academic_years SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class AcademicYear extends AuditableSoftDeleteEntity {

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
