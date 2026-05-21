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
@Table(name = "admission_emis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE admission_emis SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class AdmissionEmi extends AuditableSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_id", nullable = false)
    private Admission admission;

    @Column(name = "emi_number", nullable = false)
    private Integer emiNumber; // 1, 2, 3 ...

    @Column(name = "emi_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal emiAmount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "fine_amount", precision = 12, scale = 2)
    private BigDecimal fineAmount = BigDecimal.ZERO;

    @Column(name = "payment_method")
    private String paymentMethod; // CASH, UPI, BANK_TRANSFER, CHEQUE, DD

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "receipt_number")
    private String receiptNumber;

    // Status: PENDING, PAID, OVERDUE, WAIVED
    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @Column(name = "remarks")
    private String remarks;
}
