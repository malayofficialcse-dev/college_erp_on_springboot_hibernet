package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "module_name"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "module_name", nullable = false)
    private String moduleName;   // e.g. "students", "employees", "payroll" …

    private boolean canView   = false;
    private boolean canCreate = false;
    private boolean canEdit   = false;
    private boolean canDelete = false;
}
