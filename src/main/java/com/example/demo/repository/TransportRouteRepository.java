package com.example.demo.repository;

import com.example.demo.model.TransportRoute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportRouteRepository extends JpaRepository<TransportRoute, Long> {
    Page<TransportRoute> findByStatus(String status, Pageable pageable);
    Page<TransportRoute> findByVehicleId(Long vehicleId, Pageable pageable);
}
