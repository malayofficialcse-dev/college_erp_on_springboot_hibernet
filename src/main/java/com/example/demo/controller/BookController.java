package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.BookIssue;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired private BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<Page<Book>> getAllBooks(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/books/available")
    public ResponseEntity<Page<Book>> getAvailableBooks(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(bookService.getAvailableBooks(pageable));
    }

    @GetMapping("/books/search")
    public ResponseEntity<Page<Book>> searchBooks(@RequestParam(required = false) String title,
                                                   @RequestParam(required = false) String author,
                                                   @PageableDefault(size = 20) Pageable pageable) {
        if (title != null) return ResponseEntity.ok(bookService.searchByTitle(title, pageable));
        if (author != null) return ResponseEntity.ok(bookService.searchByAuthor(author, pageable));
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @PostMapping("/books")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(book));
    }

    @PutMapping("/books/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book details) {
        return ResponseEntity.ok(bookService.updateBook(id, details));
    }

    @PostMapping("/issues")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<BookIssue> issueBook(@RequestBody BookIssue issue) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.issueBook(issue));
    }

    @PatchMapping("/issues/{issueId}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<BookIssue> returnBook(@PathVariable Long issueId) {
        return ResponseEntity.ok(bookService.returnBook(issueId));
    }

    @GetMapping("/issues/student/{studentId}")
    public ResponseEntity<Page<BookIssue>> getIssuesByStudent(@PathVariable Long studentId,
                                                               @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(bookService.getIssuesByStudent(studentId, pageable));
    }

    @GetMapping("/issues/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Page<BookIssue>> getOverdue(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(bookService.getOverdueIssues(pageable));
    }
}
