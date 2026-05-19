package com.example.demo.controller;

import com.example.demo.model.TransportAllocation;
import com.example.demo.model.TransportRoute;
import com.example.demo.model.Vehicle;
import com.example.demo.service.TransportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transport")
@CrossOrigin(origins = "*")
public class TransportController {

    @Autowired private TransportService transportService;

    // ---- Vehicles ----
    @GetMapping("/vehicles")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<Vehicle>> getAllVehicles(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(transportService.getAllVehicles(pageable));
    }

    @GetMapping("/vehicles/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(transportService.getVehicleById(id));
    }

    @PostMapping("/vehicles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody Vehicle vehicle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transportService.saveVehicle(vehicle));
    }

    @PutMapping("/vehicles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle details) {
        return ResponseEntity.ok(transportService.updateVehicle(id, details));
    }

    @DeleteMapping("/vehicles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        transportService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Routes ----
    @GetMapping("/routes")
    public ResponseEntity<Page<TransportRoute>> getAllRoutes(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(transportService.getAllRoutes(pageable));
    }

    @GetMapping("/routes/{id}")
    public ResponseEntity<TransportRoute> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(transportService.getRouteById(id));
    }

    @PostMapping("/routes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransportRoute> createRoute(@Valid @RequestBody TransportRoute route) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transportService.saveRoute(route));
    }

    @PutMapping("/routes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransportRoute> updateRoute(@PathVariable Long id, @RequestBody TransportRoute details) {
        return ResponseEntity.ok(transportService.updateRoute(id, details));
    }

    @DeleteMapping("/routes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        transportService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Allocations ----
    @GetMapping("/allocations")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<TransportAllocation>> getAllAllocations(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(transportService.getAllAllocations(pageable));
    }

    @GetMapping("/allocations/student/{studentId}")
    public ResponseEntity<Page<TransportAllocation>> getByStudent(@PathVariable Long studentId,
                                                                   @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(transportService.getByStudent(studentId, pageable));
    }

    @PostMapping("/allocations")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<TransportAllocation> allocateTransport(@Valid @RequestBody TransportAllocation allocation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transportService.allocateTransport(allocation));
    }

    @PatchMapping("/allocations/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<TransportAllocation> updateAllocationStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(transportService.updateAllocationStatus(id, status));
    }

    @DeleteMapping("/allocations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAllocation(@PathVariable Long id) {
        transportService.deleteAllocation(id);
        return ResponseEntity.noContent().build();
    }
}
