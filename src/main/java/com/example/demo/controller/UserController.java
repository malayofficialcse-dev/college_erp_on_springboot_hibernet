package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.UserPermission;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User details) {
        return ResponseEntity.ok(userService.updateUser(id, details));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserPermission>> getUserPermissions(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserPermissions(id));
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserPermission>> updateUserPermissions(
            @PathVariable Long id, 
            @RequestBody List<UserPermission> permissions) {
        return ResponseEntity.ok(userService.updateUserPermissions(id, permissions));
    }

    @GetMapping("/me/permissions")
    public ResponseEntity<List<UserPermission>> getMyPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getUserPermissions(userDetails.getId()));
    }
}
