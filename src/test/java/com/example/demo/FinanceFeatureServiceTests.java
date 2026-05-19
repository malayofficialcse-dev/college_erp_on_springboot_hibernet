package com.example.demo;

import com.example.demo.dto.finance.FeeInvoiceRequest;
import com.example.demo.dto.finance.FeeInvoiceResponse;
import com.example.demo.dto.finance.FeePaymentTransactionRequest;
import com.example.demo.model.AcademicYear;
import com.example.demo.model.Course;
import com.example.demo.model.Department;
import com.example.demo.model.FeeStructure;
import com.example.demo.model.Scholarship;
import com.example.demo.model.Student;
import com.example.demo.repository.AcademicYearRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.FeeStructureRepository;
import com.example.demo.repository.ScholarshipRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.FinanceFeatureService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FinanceFeatureServiceTests {

    @Autowired private FinanceFeatureService financeFeatureService;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private AcademicYearRepository academicYearRepository;
    @Autowired private FeeStructureRepository feeStructureRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private ScholarshipRepository scholarshipRepository;

    @Test
    void invoiceGenerationAppliesScholarshipAndSupportsPartialPayment() {
        Department department = departmentRepository.save(new Department(null, "Finance Test Dept", "FTD", null, 2020, "ACTIVE", null));
        Course course = new Course();
        course.setCourseCode("FIN101");
        course.setTitle("Finance Course");
        course.setDepartment(department);
        course.setTotalSemesters(6);
        course = courseRepository.save(course);

        AcademicYear academicYear = new AcademicYear();
        academicYear.setYearLabel("2026-2027");
        academicYear.setStartYear(2026);
        academicYear.setEndYear(2027);
        academicYear = academicYearRepository.save(academicYear);

        FeeStructure feeStructure = new FeeStructure();
        feeStructure.setCourse(course);
        feeStructure.setAcademicYear(academicYear);
        feeStructure.setSemesterNumber(1);
        feeStructure.setTuitionFee(BigDecimal.valueOf(1000));
        feeStructure.setExamFee(BigDecimal.valueOf(200));
        feeStructure.setLibraryFee(BigDecimal.valueOf(100));
        feeStructure = feeStructureRepository.save(feeStructure);

        Student student = new Student();
        student.setEnrollmentNumber("FIN-STU-001");
        student.setFirstName("Fee");
        student.setLastName("Tester");
        student.setEmail("fee.tester@example.com");
        student.setDepartment(department);
        student.setCurrentSemester(1);
        student.setCourses(Set.of(course));
        student = studentRepository.save(student);

        Scholarship scholarship = new Scholarship();
        scholarship.setScholarshipName("Merit");
        scholarship.setStudent(student);
        scholarship.setAcademicYear(academicYear);
        scholarship.setAmount(BigDecimal.valueOf(300));
        scholarship.setStatus("ACTIVE");
        scholarshipRepository.save(scholarship);

        FeeInvoiceResponse invoice = financeFeatureService.generateInvoice(
                new FeeInvoiceRequest(student.getId(), feeStructure.getId(), LocalDate.now().plusDays(7), "Test invoice"));

        assertBigDecimalEquals("1300.00", invoice.baseAmount());
        assertBigDecimalEquals("300.00", invoice.scholarshipAdjustment());
        assertBigDecimalEquals("1000.00", invoice.totalAmount());

        FeeInvoiceResponse afterPayment = financeFeatureService.recordPartialPayment(
                invoice.id(),
                new FeePaymentTransactionRequest(BigDecimal.valueOf(400), LocalDate.now(), "ONLINE", "TXN-1", "partial"));

        assertBigDecimalEquals("400.00", afterPayment.paidAmount());
        assertBigDecimalEquals("600.00", afterPayment.outstandingAmount());
        assertEquals("PARTIAL", afterPayment.status());
    }

    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertTrue(actual.compareTo(new BigDecimal(expected)) == 0,
                () -> "Expected " + expected + " but was " + actual);
    }
}
