package com.example.demo.dto;

import com.example.demo.model.ERole;
import lombok.Data;

import java.util.Set;

@Data
public class UserManagementDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String employeeCode;
    private Long employeeId;
    private boolean enabled;
    private Set<ERole> roles;
}
