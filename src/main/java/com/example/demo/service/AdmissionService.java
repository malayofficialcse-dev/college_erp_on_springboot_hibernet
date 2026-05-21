package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Admission;
import com.example.demo.model.AdmissionEmi;
import com.example.demo.model.Course;
import com.example.demo.model.Department;
import com.example.demo.model.Student;
import com.example.demo.repository.AdmissionEmiRepository;
import com.example.demo.repository.AdmissionRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AdmissionService {

    @Autowired private AdmissionRepository admissionRepository;
    @Autowired private AdmissionEmiRepository admissionEmiRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private DepartmentRepository departmentRepository;

    // ───── READ ─────────────────────────────────────────────────────────────────

    public Page<Admission> getAll(Pageable pageable) {
        return admissionRepository.findAll(pageable);
    }

    public Admission getById(Long id) {
        return admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission", "id", id));
    }

    public Page<Admission> search(Long studentId, Long courseId, Long departmentId,
                                  String status, String academicYear, String paymentPlan,
                                  String keyword, Pageable pageable) {
        return admissionRepository.search(
                studentId,
                courseId,
                departmentId,
                emptyToNull(status),
                emptyToNull(academicYear),
                emptyToNull(paymentPlan),
                emptyToNull(keyword),
                pageable
        );
    }

    public List<Admission> getByStudent(Long studentId) {
        return admissionRepository.findByStudentIdOrderByAdmissionDateDesc(studentId);
    }

    public List<AdmissionEmi> getEmiSchedule(Long admissionId) {
        return admissionEmiRepository.findByAdmissionIdOrderByEmiNumber(admissionId);
    }

    // ───── CREATE ────────────────────────────────────────────────────────────────

    @Transactional
    public Admission createAdmission(Admission admission) {
        // Validate and attach relationships
        Student student = studentRepository.findById(admission.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", admission.getStudent().getId()));
        Course course = courseRepository.findById(admission.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", admission.getCourse().getId()));

        admission.setStudent(student);
        admission.setCourse(course);

        if (admission.getDepartment() != null && admission.getDepartment().getId() != null) {
            Department dept = departmentRepository.findById(admission.getDepartment().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", admission.getDepartment().getId()));
            admission.setDepartment(dept);
        }

        // Generate admission number
        if (admission.getAdmissionNumber() == null || admission.getAdmissionNumber().isBlank()) {
            String year = String.valueOf(LocalDate.now().getYear());
            String seq = String.format("%04d", (admissionRepository.count() + 1));
            admission.setAdmissionNumber("ADM-" + year + "-" + seq);
        }

        // Set admission date if not provided
        if (admission.getAdmissionDate() == null) {
            admission.setAdmissionDate(LocalDate.now());
        }

        // Calculate net payable
        BigDecimal discount = admission.getDiscountAmount() != null ? admission.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal netPayable = admission.getTotalFeeAmount().subtract(discount);
        admission.setNetPayableAmount(netPayable);

        // Calculate initial balance
        BigDecimal advance = admission.getAdvanceAmount() != null ? admission.getAdvanceAmount() : BigDecimal.ZERO;
        admission.setAmountPaid(advance);
        admission.setBalanceDue(netPayable.subtract(advance));
        
        // Generate bill number (BILL/SESSION/XXXXX)
        if (admission.getBillNumber() == null || admission.getBillNumber().isBlank()) {
            String session = admission.getAcademicYear() != null && !admission.getAcademicYear().isBlank() 
                                ? admission.getAcademicYear() 
                                : String.valueOf(LocalDate.now().getYear());
            long count = admissionRepository.countByAcademicYear(session);
            String seq = String.format("%05d", (count + 1));
            admission.setBillNumber("BILL/" + session + "/" + seq);
        }

        Admission saved = admissionRepository.save(admission);

        // Auto-generate EMI schedule if plan is EMI
        if ("EMI".equalsIgnoreCase(saved.getPaymentPlan()) && saved.getNumberOfEmis() != null && saved.getNumberOfEmis() > 0) {
            generateEmiSchedule(saved);
        }

        return admissionRepository.findById(saved.getId()).orElse(saved);
    }

    private void generateEmiSchedule(Admission admission) {
        BigDecimal remainingAfterAdvance = admission.getNetPayableAmount().subtract(
                admission.getAdvanceAmount() != null ? admission.getAdvanceAmount() : BigDecimal.ZERO
        );
        int numEmis = admission.getNumberOfEmis();
        BigDecimal emiAmount = remainingAfterAdvance.divide(BigDecimal.valueOf(numEmis), 2, RoundingMode.HALF_UP);
        LocalDate startDate = admission.getAdmissionDate().plusMonths(1).withDayOfMonth(1);

        for (int i = 1; i <= numEmis; i++) {
            AdmissionEmi emi = new AdmissionEmi();
            emi.setAdmission(admission);
            emi.setEmiNumber(i);
            emi.setEmiAmount(emiAmount);
            emi.setDueDate(startDate.plusMonths(i - 1));
            emi.setStatus("PENDING");
            admissionEmiRepository.save(emi);
        }
    }

    // ───── UPDATE ────────────────────────────────────────────────────────────────

    @Transactional
    public Admission updateAdmission(Long id, Admission details) {
        Admission existing = getById(id);
        existing.setAcademicYear(details.getAcademicYear());
        existing.setStatus(details.getStatus());
        existing.setRemarks(details.getRemarks());
        existing.setDiscountAmount(details.getDiscountAmount() != null ? details.getDiscountAmount() : BigDecimal.ZERO);
        BigDecimal netPayable = existing.getTotalFeeAmount().subtract(existing.getDiscountAmount());
        existing.setNetPayableAmount(netPayable);
        existing.setBalanceDue(netPayable.subtract(existing.getAmountPaid()));
        return admissionRepository.save(existing);
    }

    // ───── EMI PAYMENT ───────────────────────────────────────────────────────────

    @Transactional
    public AdmissionEmi recordEmiPayment(Long emiId, AdmissionEmi paymentDetails) {
        AdmissionEmi emi = admissionEmiRepository.findById(emiId)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionEmi", "id", emiId));

        emi.setPaidDate(paymentDetails.getPaidDate() != null ? paymentDetails.getPaidDate() : LocalDate.now());
        emi.setPaidAmount(paymentDetails.getPaidAmount() != null ? paymentDetails.getPaidAmount() : emi.getEmiAmount());
        emi.setFineAmount(paymentDetails.getFineAmount() != null ? paymentDetails.getFineAmount() : BigDecimal.ZERO);
        emi.setPaymentMethod(paymentDetails.getPaymentMethod());
        emi.setTransactionId(paymentDetails.getTransactionId());
        emi.setReceiptNumber(paymentDetails.getReceiptNumber() != null ? paymentDetails.getReceiptNumber()
                : "EMI-RCP-" + emiId + "-" + System.currentTimeMillis());
        emi.setStatus("PAID");
        emi.setRemarks(paymentDetails.getRemarks());

        AdmissionEmi savedEmi = admissionEmiRepository.save(emi);

        // Update parent admission amounts
        Admission admission = emi.getAdmission();
        BigDecimal totalPaid = admissionEmiRepository.sumPaidByAdmission(admission.getId())
                .add(admission.getAdvanceAmount() != null ? admission.getAdvanceAmount() : BigDecimal.ZERO);
        admission.setAmountPaid(totalPaid);
        admission.setBalanceDue(admission.getNetPayableAmount().subtract(totalPaid));

        // Check if fully paid
        long pendingCount = admissionEmiRepository.countByAdmissionIdAndStatus(admission.getId(), "PENDING");
        if (pendingCount == 0) {
            admission.setStatus("COMPLETED");
        }
        admissionRepository.save(admission);

        return savedEmi;
    }

    // ───── DELETE ────────────────────────────────────────────────────────────────

    @Transactional
    public void deleteAdmission(Long id) {
        Admission a = getById(id);
        admissionRepository.delete(a);
    }

    // ───── HELPERS ───────────────────────────────────────────────────────────────

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
