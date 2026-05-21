package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE admissions SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Admission extends AuditableSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admission_number", nullable = false, unique = true)
    private String admissionNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "academic_year", nullable = false)
    private String academicYear; // e.g. "2025-26"

    @Column(name = "admission_date", nullable = false)
    private LocalDate admissionDate;

    @Column(name = "total_fee_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalFeeAmount;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "net_payable_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal netPayableAmount;

    @Column(name = "amount_paid", precision = 12, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "balance_due", precision = 12, scale = 2)
    private BigDecimal balanceDue;

    // Payment Plan: FULL or EMI
    @Column(name = "payment_plan", nullable = false)
    private String paymentPlan = "FULL"; // FULL, EMI

    @Column(name = "number_of_emis")
    private Integer numberOfEmis; // null for FULL plan

    // Initial payment / advance paid at admission
    @Column(name = "advance_amount", precision = 12, scale = 2)
    private BigDecimal advanceAmount = BigDecimal.ZERO;

    @Column(name = "advance_payment_date")
    private LocalDate advancePaymentDate;

    @Column(name = "advance_payment_method")
    private String advancePaymentMethod; // CASH, UPI, BANK_TRANSFER, CHEQUE, DD

    @Column(name = "advance_transaction_id")
    private String advanceTransactionId;

    // Status: PENDING, ACTIVE, COMPLETED, CANCELLED
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "bill_number", unique = true)
    private String billNumber;

    @OneToMany(mappedBy = "admission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdmissionEmi> emiSchedule = new ArrayList<>();
}
