package com.example.demo.repository;

import com.example.demo.model.FeeInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeeInvoiceRepository extends JpaRepository<FeeInvoice, Long> {
    Page<FeeInvoice> findByStudentId(Long studentId, Pageable pageable);
    Page<FeeInvoice> findByStatus(String status, Pageable pageable);
    Optional<FeeInvoice> findByInvoiceNumber(String invoiceNumber);
}
