package com.example.demo.repository;

import com.example.demo.model.HostelRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HostelRoomRepository extends JpaRepository<HostelRoom, Long> {
    Optional<HostelRoom> findByRoomNumber(String roomNumber);
    Page<HostelRoom> findByHostelBlock(String hostelBlock, Pageable pageable);
    Page<HostelRoom> findByStatus(String status, Pageable pageable);
    Page<HostelRoom> findByRoomType(String roomType, Pageable pageable);
}
