package com.example.demo.service;

import com.example.demo.dto.finance.FeeInvoiceRequest;
import com.example.demo.dto.finance.FeeInvoiceResponse;
import com.example.demo.dto.finance.FeePaymentTransactionRequest;
import com.example.demo.dto.finance.PayrollSlipResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.FeeInvoice;
import com.example.demo.model.FeePaymentTransaction;
import com.example.demo.model.FeeStructure;
import com.example.demo.model.Payroll;
import com.example.demo.model.Scholarship;
import com.example.demo.model.Student;
import com.example.demo.repository.FeeInvoiceRepository;
import com.example.demo.repository.FeePaymentTransactionRepository;
import com.example.demo.repository.FeeStructureRepository;
import com.example.demo.repository.PayrollRepository;
import com.example.demo.repository.ScholarshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class FinanceFeatureService {

    private static final BigDecimal DAILY_LATE_FEE = BigDecimal.valueOf(25);

    @Autowired private StudentService studentService;
    @Autowired private FeeStructureRepository feeStructureRepository;
    @Autowired private ScholarshipRepository scholarshipRepository;
    @Autowired private FeeInvoiceRepository feeInvoiceRepository;
    @Autowired private FeePaymentTransactionRepository feePaymentTransactionRepository;
    @Autowired private PayrollRepository payrollRepository;

    public Page<FeeInvoice> getInvoices(Pageable pageable) {
        return feeInvoiceRepository.findAll(pageable);
    }

    public Page<FeeInvoice> getInvoicesByStudent(Long studentId, Pageable pageable) {
        return feeInvoiceRepository.findByStudentId(studentId, pageable);
    }

    public FeeInvoiceResponse getInvoice(Long invoiceId) {
        return toResponse(getInvoiceEntity(invoiceId));
    }

    @Transactional
    public FeeInvoiceResponse generateInvoice(FeeInvoiceRequest request) {
        Student student = studentService.getStudentById(request.studentId());
        FeeStructure feeStructure = feeStructureRepository.findById(request.feeStructureId())
                .orElseThrow(() -> new ResourceNotFoundException("FeeStructure", "id", request.feeStructureId()));

        BigDecimal baseAmount = safe(feeStructure.getTuitionFee())
                .add(safe(feeStructure.getExamFee()))
                .add(safe(feeStructure.getLibraryFee()))
                .add(safe(feeStructure.getHostelFee()))
                .add(safe(feeStructure.getTransportFee()))
                .add(safe(feeStructure.getOtherFee()));

        BigDecimal scholarshipAdjustment = calculateScholarshipAdjustment(student.getId(), feeStructure.getAcademicYear().getId());
        BigDecimal lateFee = calculateLateFee(request.dueDate());
        BigDecimal totalAmount = baseAmount.subtract(scholarshipAdjustment).max(BigDecimal.ZERO).add(lateFee);

        FeeInvoice invoice = new FeeInvoice();
        invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setStudent(student);
        invoice.setFeeStructure(feeStructure);
        invoice.setSemesterNumber(feeStructure.getSemesterNumber());
        invoice.setBaseAmount(baseAmount);
        invoice.setScholarshipAdjustment(scholarshipAdjustment);
        invoice.setLateFee(lateFee);
        invoice.setTotalAmount(totalAmount);
        invoice.setPaidAmount(BigDecimal.ZERO);
        invoice.setOutstandingAmount(totalAmount);
        invoice.setDueDate(request.dueDate());
        invoice.setStatus(totalAmount.compareTo(BigDecimal.ZERO) == 0 ? "PAID" : request.dueDate().isBefore(LocalDate.now()) ? "OVERDUE" : "PENDING");
        invoice.setRemarks(request.remarks());
        return toResponse(feeInvoiceRepository.save(invoice));
    }

    @Transactional
    public FeeInvoiceResponse recordPartialPayment(Long invoiceId, FeePaymentTransactionRequest request) {
        FeeInvoice invoice = getInvoiceEntity(invoiceId);
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        if (request.amount().compareTo(invoice.getOutstandingAmount()) > 0) {
            throw new IllegalArgumentException("Payment amount cannot exceed outstanding balance.");
        }

        FeePaymentTransaction transaction = new FeePaymentTransaction();
        transaction.setInvoice(invoice);
        transaction.setAmount(request.amount());
        transaction.setPaymentDate(request.paymentDate() != null ? request.paymentDate() : LocalDate.now());
        transaction.setPaymentMethod(request.paymentMethod());
        transaction.setReferenceNumber(request.referenceNumber());
        transaction.setRemarks(request.remarks());
        feePaymentTransactionRepository.save(transaction);

        invoice.setPaidAmount(invoice.getPaidAmount().add(request.amount()));
        invoice.setOutstandingAmount(invoice.getTotalAmount().subtract(invoice.getPaidAmount()));
        invoice.setStatus(invoice.getOutstandingAmount().compareTo(BigDecimal.ZERO) == 0 ? "PAID" : "PARTIAL");
        return toResponse(feeInvoiceRepository.save(invoice));
    }

    public PayrollSlipResponse generatePayrollSlip(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll", "id", payrollId));
        return new PayrollSlipResponse(
                payroll.getId(),
                payroll.getEmployee().getEmployeeCode(),
                payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName(),
                payroll.getEmployee().getDesignation(),
                payroll.getPayMonth(),
                payroll.getPayYear(),
                payroll.getBasicSalary(),
                safe(payroll.getHra()),
                safe(payroll.getDa()),
                safe(payroll.getTa()),
                safe(payroll.getOtherAllowances()),
                safe(payroll.getBonus()),
                safe(payroll.getGrossSalary()),
                safe(payroll.getPfDeduction()),
                safe(payroll.getEsiDeduction()),
                safe(payroll.getTaxDeduction()),
                safe(payroll.getOtherDeductions()),
                safe(payroll.getNetSalary()),
                payroll.getStatus()
        );
    }

    private FeeInvoice getInvoiceEntity(Long invoiceId) {
        return feeInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeInvoice", "id", invoiceId));
    }

    private BigDecimal calculateScholarshipAdjustment(Long studentId, Long academicYearId) {
        List<Scholarship> scholarships = scholarshipRepository.findByStudentIdAndAcademicYearIdAndStatusIgnoreCase(studentId, academicYearId, "ACTIVE");
        if (scholarships.isEmpty()) {
            scholarships = scholarshipRepository.findByStudentIdAndStatusIgnoreCase(studentId, "ACTIVE");
        }
        return scholarships.stream().map(Scholarship::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateLateFee(LocalDate dueDate) {
        if (dueDate == null || !dueDate.isBefore(LocalDate.now())) {
            return BigDecimal.ZERO;
        }
        long daysLate = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        return DAILY_LATE_FEE.multiply(BigDecimal.valueOf(Math.max(daysLate, 0)));
    }

    private FeeInvoiceResponse toResponse(FeeInvoice invoice) {
        List<FeeInvoiceResponse.PaymentHistoryItem> payments = feePaymentTransactionRepository.findByInvoiceIdOrderByPaymentDateAscIdAsc(invoice.getId())
                .stream()
                .map(tx -> new FeeInvoiceResponse.PaymentHistoryItem(tx.getId(), tx.getAmount(), tx.getPaymentDate(),
                        tx.getPaymentMethod(), tx.getReferenceNumber(), tx.getStatus()))
                .toList();
        return new FeeInvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getStudent().getId(),
                invoice.getStudent().getFirstName() + " " + invoice.getStudent().getLastName(),
                "Semester " + invoice.getSemesterNumber(),
                invoice.getBaseAmount(),
                invoice.getScholarshipAdjustment(),
                invoice.getLateFee(),
                invoice.getTotalAmount(),
                invoice.getPaidAmount(),
                invoice.getOutstandingAmount(),
                invoice.getDueDate(),
                invoice.getStatus(),
                payments
        );
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
