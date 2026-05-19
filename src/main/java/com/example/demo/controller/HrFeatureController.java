package com.example.demo.controller;

import com.example.demo.model.EmployeeAttendance;
import com.example.demo.model.EmployeeDocument;
import com.example.demo.model.LeaveApprovalStep;
import com.example.demo.model.LeaveRequest;
import com.example.demo.service.HrFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr")
@CrossOrigin(origins = "*")
public class HrFeatureController {

    @Autowired private HrFeatureService hrFeatureService;

    @GetMapping("/attendance")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'HOD')")
    public ResponseEntity<Page<EmployeeAttendance>> getAttendance(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(hrFeatureService.getAttendance(pageable));
    }

    @GetMapping("/attendance/employee/{employeeId}")
    public ResponseEntity<Page<EmployeeAttendance>> getAttendanceByEmployee(@PathVariable Long employeeId,
                                                                            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(hrFeatureService.getAttendanceByEmployee(employeeId, pageable));
    }

    @PostMapping("/attendance")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'HOD')")
    public ResponseEntity<EmployeeAttendance> markAttendance(@RequestBody EmployeeAttendance attendance) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hrFeatureService.markAttendance(attendance));
    }

    @GetMapping("/documents/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'HOD')")
    public ResponseEntity<List<EmployeeDocument>> getDocuments(@PathVariable Long employeeId) {
        return ResponseEntity.ok(hrFeatureService.getDocuments(employeeId));
    }

    @PostMapping("/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'HOD')")
    public ResponseEntity<EmployeeDocument> saveDocument(@RequestBody EmployeeDocument document) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hrFeatureService.saveDocument(document));
    }

    @PostMapping("/leaves/{leaveRequestId}/workflow")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'HOD')")
    public ResponseEntity<LeaveRequest> setupWorkflow(@PathVariable Long leaveRequestId,
                                                      @RequestBody List<Long> approverIds) {
        return ResponseEntity.ok(hrFeatureService.setupApprovalWorkflow(leaveRequestId, approverIds));
    }

    @GetMapping("/leaves/{leaveRequestId}/workflow")
    public ResponseEntity<List<LeaveApprovalStep>> getWorkflow(@PathVariable Long leaveRequestId) {
        return ResponseEntity.ok(hrFeatureService.getApprovalSteps(leaveRequestId));
    }

    @PatchMapping("/leave-approval-steps/{stepId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'HOD')")
    public ResponseEntity<LeaveRequest> actionStep(@PathVariable Long stepId,
                                                   @RequestParam String status,
                                                   @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(hrFeatureService.actionApprovalStep(stepId, status, remarks));
    }
}
