package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fee_invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE fee_invoices SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class FeeInvoice extends AuditableSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    @Column(name = "semester_number", nullable = false)
    private Integer semesterNumber;

    @Column(name = "base_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal baseAmount;

    @Column(name = "scholarship_adjustment", precision = 12, scale = 2, nullable = false)
    private BigDecimal scholarshipAdjustment = BigDecimal.ZERO;

    @Column(name = "late_fee", precision = 12, scale = 2, nullable = false)
    private BigDecimal lateFee = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "outstanding_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal outstandingAmount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @Column(name = "remarks")
    private String remarks;
}
