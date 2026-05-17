package com.example.demo.service;

import com.example.demo.model.FeePayment;
import com.example.demo.repository.FeePaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeePaymentService {

    @Autowired
    private FeePaymentRepository feePaymentRepository;

    public List<FeePayment> getAllFeePayments() {
        return feePaymentRepository.findAll();
    }

    public FeePayment getFeePaymentById(Long id) {
        return feePaymentRepository.findById(id).orElse(null);
    }

    public FeePayment saveFeePayment(FeePayment feePayment) {
        return feePaymentRepository.save(feePayment);
    }

    public FeePayment updateFeePayment(Long id, FeePayment feePaymentDetails) {
        Optional<FeePayment> feePayment = feePaymentRepository.findById(id);
        if (feePayment.isPresent()) {
            FeePayment existing = feePayment.get();
            existing.setStudent(feePaymentDetails.getStudent());
            existing.setAmount(feePaymentDetails.getAmount());
            existing.setPaymentDate(feePaymentDetails.getPaymentDate());
            existing.setStatus(feePaymentDetails.getStatus());
            existing.setSemester(feePaymentDetails.getSemester());
            return feePaymentRepository.save(existing);
        }
        return null;
    }

    public void deleteFeePayment(Long id) {
        feePaymentRepository.deleteById(id);
    }
}
