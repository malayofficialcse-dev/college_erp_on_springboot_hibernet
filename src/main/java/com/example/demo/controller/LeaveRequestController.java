package com.example.demo.controller;

import com.example.demo.model.LeaveRequest;
import com.example.demo.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin(origins = "*")
public class LeaveRequestController {

    @Autowired private LeaveRequestService leaveRequestService;

    @GetMapping
    public ResponseEntity<Page<LeaveRequest>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.getById(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<LeaveRequest>> getByEmployee(@PathVariable Long employeeId,
                                                             @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(leaveRequestService.getByEmployee(employeeId, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<LeaveRequest>> getByStatus(@PathVariable String status,
                                                           @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(leaveRequestService.getByStatus(status, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<LeaveRequest>> search(@RequestParam(required = false) Long employeeId,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(required = false) String leaveType,
                                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                                     @RequestParam(required = false) String keyword,
                                                     @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(leaveRequestService.search(employeeId, status, leaveType, dateFrom, dateTo, keyword, pageable));
    }

    @PostMapping
    public ResponseEntity<LeaveRequest> apply(@RequestBody LeaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveRequestService.applyLeave(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LeaveRequest> updateStatus(@PathVariable Long id,
                                                      @RequestParam String status,
                                                      @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(leaveRequestService.updateStatus(id, status, remarks));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leaveRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
