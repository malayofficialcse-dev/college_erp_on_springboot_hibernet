package com.example.demo.repository;

import com.example.demo.model.HostelAllocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostelAllocationRepository extends JpaRepository<HostelAllocation, Long> {
    Page<HostelAllocation> findByStudentId(Long studentId, Pageable pageable);
    Page<HostelAllocation> findByHostelRoomId(Long roomId, Pageable pageable);
    Page<HostelAllocation> findByStatus(String status, Pageable pageable);
    Page<HostelAllocation> findByAcademicYearId(Long academicYearId, Pageable pageable);
    long countByHostelRoomIdAndStatus(Long roomId, String status);
}
