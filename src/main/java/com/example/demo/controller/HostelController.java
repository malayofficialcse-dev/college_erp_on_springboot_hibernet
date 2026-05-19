package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.HostelAllocation;
import com.example.demo.model.HostelRoom;
import com.example.demo.repository.HostelAllocationRepository;
import com.example.demo.repository.HostelRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hostel")
@CrossOrigin(origins = "*")
public class HostelController {

    @Autowired private HostelRoomRepository hostelRoomRepository;
    @Autowired private HostelAllocationRepository hostelAllocationRepository;

    // ---- Rooms ----
    @GetMapping("/rooms")
    public ResponseEntity<Page<HostelRoom>> getAllRooms(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(hostelRoomRepository.findAll(pageable));
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<HostelRoom> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(hostelRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HostelRoom", "id", id)));
    }

    @GetMapping("/rooms/available")
    public ResponseEntity<Page<HostelRoom>> getAvailableRooms(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(hostelRoomRepository.findByStatus("AVAILABLE", pageable));
    }

    @PostMapping("/rooms")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSTEL_WARDEN')")
    public ResponseEntity<HostelRoom> addRoom(@RequestBody HostelRoom room) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hostelRoomRepository.save(room));
    }

    @PutMapping("/rooms/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSTEL_WARDEN')")
    public ResponseEntity<HostelRoom> updateRoom(@PathVariable Long id, @RequestBody HostelRoom details) {
        HostelRoom room = hostelRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HostelRoom", "id", id));
        room.setRoomType(details.getRoomType());
        room.setCapacity(details.getCapacity());
        room.setMonthlyRent(details.getMonthlyRent());
        room.setFacilities(details.getFacilities());
        room.setStatus(details.getStatus());
        return ResponseEntity.ok(hostelRoomRepository.save(room));
    }

    // ---- Allocations ----
    @GetMapping("/allocations")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSTEL_WARDEN')")
    public ResponseEntity<Page<HostelAllocation>> getAllAllocations(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(hostelAllocationRepository.findAll(pageable));
    }

    @GetMapping("/allocations/student/{studentId}")
    public ResponseEntity<Page<HostelAllocation>> getByStudent(@PathVariable Long studentId,
                                                                @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(hostelAllocationRepository.findByStudentId(studentId, pageable));
    }

    @PostMapping("/allocations")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSTEL_WARDEN')")
    @Transactional
    public ResponseEntity<HostelAllocation> allocate(@RequestBody HostelAllocation allocation) {
        HostelRoom room = hostelRoomRepository.findById(allocation.getHostelRoom().getId())
                .orElseThrow(() -> new ResourceNotFoundException("HostelRoom", "id", allocation.getHostelRoom().getId()));
        if (room.getOccupied() >= room.getCapacity()) {
            throw new IllegalArgumentException("Room is full: " + room.getRoomNumber());
        }
        room.setOccupied(room.getOccupied() + 1);
        if (room.getOccupied().equals(room.getCapacity())) room.setStatus("FULL");
        hostelRoomRepository.save(room);
        allocation.setStatus("ACTIVE");
        return ResponseEntity.status(HttpStatus.CREATED).body(hostelAllocationRepository.save(allocation));
    }

    @PatchMapping("/allocations/{id}/vacate")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSTEL_WARDEN')")
    @Transactional
    public ResponseEntity<HostelAllocation> vacate(@PathVariable Long id) {
        HostelAllocation allocation = hostelAllocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HostelAllocation", "id", id));
        allocation.setStatus("VACATED");
        HostelRoom room = allocation.getHostelRoom();
        room.setOccupied(Math.max(0, room.getOccupied() - 1));
        room.setStatus("AVAILABLE");
        hostelRoomRepository.save(room);
        return ResponseEntity.ok(hostelAllocationRepository.save(allocation));
    }
}
