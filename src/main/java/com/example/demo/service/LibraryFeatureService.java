package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Book;
import com.example.demo.model.BookIssue;
import com.example.demo.model.BookReservation;
import com.example.demo.repository.BookIssueRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.BookReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LibraryFeatureService {

    private static final BigDecimal DAILY_OVERDUE_FINE = BigDecimal.valueOf(5);
    private static final int LOW_STOCK_THRESHOLD = 2;

    @Autowired private BookRepository bookRepository;
    @Autowired private BookIssueRepository bookIssueRepository;
    @Autowired private BookReservationRepository reservationRepository;

    @Transactional
    public BookReservation reserveBook(BookReservation reservation) {
        Book book = bookRepository.findById(reservation.getBook().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", reservation.getBook().getId()));
        if (reservation.getStudent() == null && reservation.getEmployee() == null) {
            throw new IllegalArgumentException("Reservation requires a student or employee.");
        }
        if (reservation.getStudent() != null
                && reservationRepository.existsByBookIdAndStudentIdAndStatus(book.getId(), reservation.getStudent().getId(), "WAITING")) {
            throw new IllegalArgumentException("Student already has an active reservation for this book.");
        }
        if (reservation.getEmployee() != null
                && reservationRepository.existsByBookIdAndEmployeeIdAndStatus(book.getId(), reservation.getEmployee().getId(), "WAITING")) {
            throw new IllegalArgumentException("Employee already has an active reservation for this book.");
        }
        int queuePosition = reservationRepository.findByBookIdAndStatusOrderByQueuePositionAsc(book.getId(), "WAITING").size() + 1;
        reservation.setBook(book);
        reservation.setQueuePosition(queuePosition);
        reservation.setRequestDate(LocalDate.now());
        reservation.setStatus("WAITING");
        return reservationRepository.save(reservation);
    }

    @Transactional
    public long markOverdueIssues() {
        List<BookIssue> issued = bookIssueRepository.findByStatus("ISSUED", Pageable.unpaged()).getContent();
        long updated = 0;
        for (BookIssue issue : issued) {
            if (issue.getDueDate() != null && issue.getDueDate().isBefore(LocalDate.now())) {
                issue.setStatus("OVERDUE");
                issue.setFineAmount(calculateOverdueFine(issue.getDueDate(), LocalDate.now()));
                bookIssueRepository.save(issue);
                updated++;
            }
        }
        return updated;
    }

    public List<BookIssue> borrowingHistoryByStudent(Long studentId) {
        return bookIssueRepository.findByStudentIdOrderByIssueDateDesc(studentId);
    }

    public List<BookIssue> borrowingHistoryByEmployee(Long employeeId) {
        return bookIssueRepository.findByEmployeeIdOrderByIssueDateDesc(employeeId);
    }

    public List<Book> lowStockBooks() {
        return bookRepository.findByAvailableCopiesLessThanEqual(LOW_STOCK_THRESHOLD);
    }

    public Map<String, Long> categoryReport() {
        return bookRepository.countByCategory().stream()
                .collect(Collectors.toMap(row -> String.valueOf(row[0]), row -> (Long) row[1]));
    }

    public Map<String, Long> departmentReport() {
        return bookRepository.countByDepartment().stream()
                .collect(Collectors.toMap(row -> String.valueOf(row[0]), row -> (Long) row[1]));
    }

    @Transactional
    public void fulfillNextReservationIfAny(Book book) {
        List<BookReservation> reservations = reservationRepository.findByBookIdAndStatusOrderByQueuePositionAsc(book.getId(), "WAITING");
        if (!reservations.isEmpty()) {
            BookReservation reservation = reservations.get(0);
            reservation.setStatus("READY_FOR_ISSUE");
            reservation.setFulfilledDate(LocalDate.now());
            reservationRepository.save(reservation);
        }
    }

    public BigDecimal calculateOverdueFine(LocalDate dueDate, LocalDate returnDate) {
        if (dueDate == null || returnDate == null || !returnDate.isAfter(dueDate)) {
            return BigDecimal.ZERO;
        }
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
        return DAILY_OVERDUE_FINE.multiply(BigDecimal.valueOf(daysLate));
    }

    public Page<BookReservation> getReservationsByBook(Long bookId, Pageable pageable) {
        return reservationRepository.findByBookId(bookId, pageable);
    }
}
