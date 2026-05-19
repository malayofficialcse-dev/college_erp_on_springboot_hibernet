package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Book;
import com.example.demo.model.BookIssue;
import com.example.demo.repository.BookIssueRepository;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;

@Service
public class BookService {

    @Autowired private BookRepository bookRepository;
    @Autowired private BookIssueRepository bookIssueRepository;

    // ---- Book CRUD ----
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
    }

    public Page<Book> searchByTitle(String title, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    public Page<Book> searchByAuthor(String author, Pageable pageable) {
        return bookRepository.findByAuthorContainingIgnoreCase(author, pageable);
    }

    public Page<Book> getAvailableBooks(Pageable pageable) {
        return bookRepository.findByAvailableCopiesGreaterThan(0, pageable);
    }

    @Transactional
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book details) {
        Book book = getBookById(id);
        book.setTitle(details.getTitle());
        book.setAuthor(details.getAuthor());
        book.setPublisher(details.getPublisher());
        book.setEdition(details.getEdition());
        book.setTotalCopies(details.getTotalCopies());
        book.setCategory(details.getCategory());
        book.setLocation(details.getLocation());
        return bookRepository.save(book);
    }

    // ---- Book Issue / Return ----
    @Transactional
    public BookIssue issueBook(BookIssue issue) {
        Book book = getBookById(issue.getBook().getId());
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalArgumentException("No copies available for: " + book.getTitle());
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        issue.setIssueDate(LocalDate.now());
        issue.setDueDate(LocalDate.now().plusDays(14)); // 14-day lending period
        issue.setStatus("ISSUED");
        return bookIssueRepository.save(issue);
    }

    @Transactional
    public BookIssue returnBook(Long issueId) {
        BookIssue issue = bookIssueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("BookIssue", "id", issueId));
        issue.setReturnDate(LocalDate.now());
        issue.setStatus("RETURNED");

        // Calculate fine if overdue (₹5 per day)
        if (LocalDate.now().isAfter(issue.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(issue.getDueDate(), LocalDate.now());
            issue.setFineAmount(BigDecimal.valueOf(daysLate * 5));
        }

        // Increment available copies
        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        return bookIssueRepository.save(issue);
    }

    public Page<BookIssue> getIssuesByStudent(Long studentId, Pageable pageable) {
        return bookIssueRepository.findByStudentId(studentId, pageable);
    }

    public Page<BookIssue> getOverdueIssues(Pageable pageable) {
        return bookIssueRepository.findByStatus("ISSUED", pageable);
    }
}
