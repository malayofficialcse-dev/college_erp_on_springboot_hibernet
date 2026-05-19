package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_approval_steps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE leave_approval_steps SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class LeaveApprovalStep extends AuditableSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_id", nullable = false)
    private LeaveRequest leaveRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private Employee approver;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @Column(name = "action_time")
    private LocalDateTime actionTime;

    @Column(name = "remarks")
    private String remarks;
}
