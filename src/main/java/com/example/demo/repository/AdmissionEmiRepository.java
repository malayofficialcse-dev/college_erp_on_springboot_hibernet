package com.example.demo.repository;

import com.example.demo.model.AdmissionEmi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdmissionEmiRepository extends JpaRepository<AdmissionEmi, Long> {

    List<AdmissionEmi> findByAdmissionIdOrderByEmiNumber(Long admissionId);

    List<AdmissionEmi> findByAdmissionIdAndStatus(Long admissionId, String status);

    List<AdmissionEmi> findByStatusAndDueDateBefore(String status, LocalDate date);

    @Query("SELECT COALESCE(SUM(e.paidAmount), 0) FROM AdmissionEmi e WHERE e.admission.id = :admissionId AND e.status = 'PAID'")
    BigDecimal sumPaidByAdmission(@Param("admissionId") Long admissionId);

    long countByAdmissionIdAndStatus(Long admissionId, String status);
}
