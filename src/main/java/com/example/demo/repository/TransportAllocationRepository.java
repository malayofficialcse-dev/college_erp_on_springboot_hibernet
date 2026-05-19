package com.example.demo.repository;

import com.example.demo.model.TransportAllocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportAllocationRepository extends JpaRepository<TransportAllocation, Long> {
    Page<TransportAllocation> findByStudentId(Long studentId, Pageable pageable);
    Page<TransportAllocation> findByRouteId(Long routeId, Pageable pageable);
    Page<TransportAllocation> findByStatus(String status, Pageable pageable);
    Page<TransportAllocation> findByAcademicYearId(Long academicYearId, Pageable pageable);
}
