package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.TransportAllocation;
import com.example.demo.model.TransportRoute;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.TransportAllocationRepository;
import com.example.demo.repository.TransportRouteRepository;
import com.example.demo.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransportService {

    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private TransportRouteRepository transportRouteRepository;
    @Autowired private TransportAllocationRepository transportAllocationRepository;

    // ---- Vehicle Operations ----
    public Page<Vehicle> getAllVehicles(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
    }

    @Transactional
    public Vehicle saveVehicle(Vehicle vehicle) {
        if (vehicleRepository.findByVehicleNumber(vehicle.getVehicleNumber()).isPresent()) {
            throw new IllegalArgumentException("Vehicle number already exists: " + vehicle.getVehicleNumber());
        }
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle details) {
        Vehicle existing = getVehicleById(id);
        existing.setVehicleType(details.getVehicleType());
        existing.setModel(details.getModel());
        existing.setCapacity(details.getCapacity());
        existing.setDriver(details.getDriver());
        existing.setStatus(details.getStatus());
        return vehicleRepository.save(existing);
    }

    @Transactional
    public void deleteVehicle(Long id) {
        getVehicleById(id);
        vehicleRepository.deleteById(id);
    }

    // ---- Route Operations ----
    public Page<TransportRoute> getAllRoutes(Pageable pageable) {
        return transportRouteRepository.findAll(pageable);
    }

    public TransportRoute getRouteById(Long id) {
        return transportRouteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransportRoute", "id", id));
    }

    @Transactional
    public TransportRoute saveRoute(TransportRoute route) {
        return transportRouteRepository.save(route);
    }

    @Transactional
    public TransportRoute updateRoute(Long id, TransportRoute details) {
        TransportRoute existing = getRouteById(id);
        existing.setRouteName(details.getRouteName());
        existing.setRouteCode(details.getRouteCode());
        existing.setStartPoint(details.getStartPoint());
        existing.setEndPoint(details.getEndPoint());
        existing.setStops(details.getStops());
        existing.setDistanceKm(details.getDistanceKm());
        existing.setMonthlyFee(details.getMonthlyFee());
        existing.setVehicle(details.getVehicle());
        existing.setStatus(details.getStatus());
        return transportRouteRepository.save(existing);
    }

    @Transactional
    public void deleteRoute(Long id) {
        getRouteById(id);
        transportRouteRepository.deleteById(id);
    }

    // ---- Allocation Operations ----
    public Page<TransportAllocation> getAllAllocations(Pageable pageable) {
        return transportAllocationRepository.findAll(pageable);
    }

    public TransportAllocation getAllocationById(Long id) {
        return transportAllocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransportAllocation", "id", id));
    }

    public Page<TransportAllocation> getByStudent(Long studentId, Pageable pageable) {
        return transportAllocationRepository.findByStudentId(studentId, pageable);
    }

    @Transactional
    public TransportAllocation allocateTransport(TransportAllocation allocation) {
        TransportRoute route = getRouteById(allocation.getRoute().getId());
        Vehicle vehicle = route.getVehicle();
        if (vehicle != null && vehicle.getCapacity() != null) {
            // Count active allocations for this route/vehicle
            long count = transportAllocationRepository.findByRouteId(route.getId(), Pageable.unpaged()).getTotalElements();
            if (count >= vehicle.getCapacity()) {
                throw new IllegalArgumentException("Assigned vehicle capacity exceeded: " + vehicle.getVehicleNumber());
            }
        }
        allocation.setStatus("ACTIVE");
        return transportAllocationRepository.save(allocation);
    }

    @Transactional
    public TransportAllocation updateAllocationStatus(Long id, String status) {
        TransportAllocation existing = getAllocationById(id);
        existing.setStatus(status);
        return transportAllocationRepository.save(existing);
    }

    @Transactional
    public void deleteAllocation(Long id) {
        getAllocationById(id);
        transportAllocationRepository.deleteById(id);
    }
}
