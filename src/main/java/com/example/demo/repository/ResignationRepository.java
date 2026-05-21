package com.example.demo.repository;

import com.example.demo.model.Resignation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResignationRepository extends JpaRepository<Resignation, Long> {
    Page<Resignation> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<Resignation> findByStatus(String status, Pageable pageable);
}
