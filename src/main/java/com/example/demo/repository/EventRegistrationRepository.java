package com.example.demo.repository;

import com.example.demo.model.EventRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    Page<EventRegistration> findByEventId(Long eventId, Pageable pageable);
    Page<EventRegistration> findByStudentId(Long studentId, Pageable pageable);
    Page<EventRegistration> findByEmployeeId(Long employeeId, Pageable pageable);
}
