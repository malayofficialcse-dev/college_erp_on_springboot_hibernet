package com.example.demo.dto;

import com.example.demo.model.ERole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {

    @NotBlank
    private String username;

    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;

    private Set<ERole> roles;
}
