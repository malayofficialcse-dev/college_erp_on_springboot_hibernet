package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.FeePayment;
import com.example.demo.repository.FeePaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class FeePaymentService {

    @Autowired private FeePaymentRepository feePaymentRepository;

    public Page<FeePayment> getAllFeePayments(Pageable pageable) {
        return feePaymentRepository.findAll(pageable);
    }

    public FeePayment getFeePaymentById(Long id) {
        return feePaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeePayment", "id", id));
    }

    public Page<FeePayment> getByStudent(Long studentId, Pageable pageable) {
        return feePaymentRepository.findByStudentId(studentId, pageable);
    }

    public Page<FeePayment> getByStatus(String status, Pageable pageable) {
        return feePaymentRepository.findByStatus(status, pageable);
    }

    public Page<FeePayment> getBySemester(String semester, Pageable pageable) {
        return feePaymentRepository.findBySemester(semester, pageable);
    }

    public Page<FeePayment> search(Long studentId, String status, String semester, LocalDate dateFrom,
                                   LocalDate dateTo, String keyword, Pageable pageable) {
        return feePaymentRepository.search(studentId, emptyToNull(status), emptyToNull(semester),
                dateFrom, dateTo, emptyToNull(keyword), pageable);
    }

    public BigDecimal getTotalPaidByStudent(Long studentId) {
        BigDecimal total = feePaymentRepository.sumPaidFeeByStudent(studentId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalCollected() {
        BigDecimal total = feePaymentRepository.sumTotalCollected();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public FeePayment saveFeePayment(FeePayment feePayment) {
        // Calculate net amount
        BigDecimal net = feePayment.getAmount()
                .subtract(feePayment.getDiscountAmount() != null ? feePayment.getDiscountAmount() : BigDecimal.ZERO)
                .add(feePayment.getFineAmount() != null ? feePayment.getFineAmount() : BigDecimal.ZERO);
        feePayment.setNetAmount(net);
        return feePaymentRepository.save(feePayment);
    }

    @Transactional
    public FeePayment updateFeePayment(Long id, FeePayment details) {
        FeePayment existing = getFeePaymentById(id);
        existing.setAmount(details.getAmount());
        existing.setDiscountAmount(details.getDiscountAmount());
        existing.setFineAmount(details.getFineAmount());
        existing.setPaymentDate(details.getPaymentDate());
        existing.setPaymentMethod(details.getPaymentMethod());
        existing.setStatus(details.getStatus());
        existing.setRemarks(details.getRemarks());
        BigDecimal net = existing.getAmount()
                .subtract(existing.getDiscountAmount() != null ? existing.getDiscountAmount() : BigDecimal.ZERO)
                .add(existing.getFineAmount() != null ? existing.getFineAmount() : BigDecimal.ZERO);
        existing.setNetAmount(net);
        return feePaymentRepository.save(existing);
    }

    @Transactional
    public void deleteFeePayment(Long id) {
        getFeePaymentById(id);
        feePaymentRepository.deleteById(id);
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
