package com.example.demo;

import com.example.demo.model.Book;
import com.example.demo.model.BookIssue;
import com.example.demo.model.BookReservation;
import com.example.demo.model.Department;
import com.example.demo.model.Student;
import com.example.demo.repository.BookIssueRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.BookReservationRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.LibraryFeatureService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LibraryFeatureServiceTests {

    @Autowired private LibraryFeatureService libraryFeatureService;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private BookIssueRepository bookIssueRepository;
    @Autowired private BookReservationRepository bookReservationRepository;

    @Test
    void reservationOverdueAndReportingFlowWorks() {
        Department department = new Department();
        department.setName("Library Test Department");
        department.setCode("LTD-" + System.nanoTime());
        department.setDescription("Department for library workflow tests");
        department.setEstablishedYear(2022);
        department.setStatus("ACTIVE");
        department = departmentRepository.save(department);

        Student student = new Student();
        student.setEnrollmentNumber("LIB-STU-" + System.nanoTime());
        student.setFirstName("Library");
        student.setLastName("Tester");
        student.setEmail("library.tester." + System.nanoTime() + "@example.com");
        student.setDepartment(department);
        student.setCurrentSemester(2);
        student = studentRepository.save(student);

        Book book = new Book();
        book.setIsbn("ISBN-" + System.nanoTime());
        book.setTitle("Distributed Systems");
        book.setAuthor("A. Author");
        book.setDepartment(department);
        book.setCategory("TEXTBOOK");
        book.setTotalCopies(2);
        book.setAvailableCopies(2);
        book = bookRepository.save(book);

        BookReservation reservation = new BookReservation();
        reservation.setBook(book);
        reservation.setStudent(student);
        BookReservation savedReservation = libraryFeatureService.reserveBook(reservation);

        assertEquals("WAITING", savedReservation.getStatus());
        assertEquals(1, savedReservation.getQueuePosition());

        BookIssue issue = new BookIssue();
        issue.setBook(book);
        issue.setStudent(student);
        issue.setIssueDate(LocalDate.now().minusDays(10));
        issue.setDueDate(LocalDate.now().minusDays(3));
        issue.setStatus("ISSUED");
        bookIssueRepository.save(issue);

        long overdueUpdated = libraryFeatureService.markOverdueIssues();
        BookIssue overdueIssue = bookIssueRepository.findById(issue.getId()).orElseThrow();

        assertEquals(1L, overdueUpdated);
        assertEquals("OVERDUE", overdueIssue.getStatus());
        assertBigDecimalEquals("15.00", overdueIssue.getFineAmount());

        book.setAvailableCopies(1);
        bookRepository.save(book);
        assertFalse(libraryFeatureService.lowStockBooks().isEmpty());

        libraryFeatureService.fulfillNextReservationIfAny(book);
        BookReservation fulfilledReservation = bookReservationRepository.findById(savedReservation.getId()).orElseThrow();

        assertEquals("READY_FOR_ISSUE", fulfilledReservation.getStatus());
        assertTrue(libraryFeatureService.categoryReport().containsKey("TEXTBOOK"));
        assertTrue(libraryFeatureService.departmentReport().containsKey("Library Test Department"));
        assertEquals(1, libraryFeatureService.borrowingHistoryByStudent(student.getId()).size());
    }

    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertTrue(actual.compareTo(new BigDecimal(expected)) == 0,
                () -> "Expected " + expected + " but was " + actual);
    }
}
