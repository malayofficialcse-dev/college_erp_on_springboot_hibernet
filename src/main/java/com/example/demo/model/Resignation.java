package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "resignations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE resignations SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Resignation extends AuditableSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "resignation_date", nullable = false)
    private LocalDate resignationDate; // Date employee submitted resignation

    @Column(name = "last_working_date")
    private LocalDate lastWorkingDate; // Proposed last day

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "status")
    private String status = "PENDING"; // PENDING, ACCEPTED, REJECTED, WITHDRAWN

    @Column(name = "hr_remarks", columnDefinition = "TEXT")
    private String hrRemarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Employee reviewedBy;

    @Column(name = "reviewed_date")
    private LocalDate reviewedDate;
}
