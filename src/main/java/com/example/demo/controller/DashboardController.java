package com.example.demo.controller;

import com.example.demo.dto.dashboard.AdminDashboardResponse;
import com.example.demo.dto.dashboard.StudentDashboardResponse;
import com.example.demo.dto.dashboard.TeacherDashboardResponse;
import com.example.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired private DashboardService dashboardService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard(@PathVariable Long studentId) {
        return ResponseEntity.ok(dashboardService.getStudentDashboard(studentId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<TeacherDashboardResponse> getTeacherDashboard(@PathVariable Long teacherId) {
        return ResponseEntity.ok(dashboardService.getTeacherDashboard(teacherId));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }
}
