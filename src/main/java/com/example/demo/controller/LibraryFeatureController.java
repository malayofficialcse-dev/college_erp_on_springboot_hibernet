package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.BookIssue;
import com.example.demo.model.BookReservation;
import com.example.demo.service.LibraryFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/library/features")
@CrossOrigin(origins = "*")
public class LibraryFeatureController {

    @Autowired private LibraryFeatureService libraryFeatureService;

    @PostMapping("/reservations")
    public ResponseEntity<BookReservation> reserve(@RequestBody BookReservation reservation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryFeatureService.reserveBook(reservation));
    }

    @GetMapping("/reservations/book/{bookId}")
    public ResponseEntity<Page<BookReservation>> getReservations(@PathVariable Long bookId,
                                                                 @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(libraryFeatureService.getReservationsByBook(bookId, pageable));
    }

    @PostMapping("/overdues/refresh")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Map<String, Long>> refreshOverdues() {
        return ResponseEntity.ok(Map.of("updated", libraryFeatureService.markOverdueIssues()));
    }

    @GetMapping("/history/student/{studentId}")
    public ResponseEntity<List<BookIssue>> studentHistory(@PathVariable Long studentId) {
        return ResponseEntity.ok(libraryFeatureService.borrowingHistoryByStudent(studentId));
    }

    @GetMapping("/history/employee/{employeeId}")
    public ResponseEntity<List<BookIssue>> employeeHistory(@PathVariable Long employeeId) {
        return ResponseEntity.ok(libraryFeatureService.borrowingHistoryByEmployee(employeeId));
    }

    @GetMapping("/reports/low-stock")
    public ResponseEntity<List<Book>> lowStock() {
        return ResponseEntity.ok(libraryFeatureService.lowStockBooks());
    }

    @GetMapping("/reports/category")
    public ResponseEntity<Map<String, Long>> categoryReport() {
        return ResponseEntity.ok(libraryFeatureService.categoryReport());
    }

    @GetMapping("/reports/department")
    public ResponseEntity<Map<String, Long>> departmentReport() {
        return ResponseEntity.ok(libraryFeatureService.departmentReport());
    }
}
