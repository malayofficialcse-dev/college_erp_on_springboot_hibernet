package com.example.demo.repository;

import com.example.demo.model.FeePaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeePaymentTransactionRepository extends JpaRepository<FeePaymentTransaction, Long> {
    List<FeePaymentTransaction> findByInvoiceIdOrderByPaymentDateAscIdAsc(Long invoiceId);
}
