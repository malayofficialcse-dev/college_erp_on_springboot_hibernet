package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hostel_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostelRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "room_number", nullable = false, unique = true)
    private String roomNumber;

    @Column(name = "room_type")
    private String roomType; // SINGLE, DOUBLE, TRIPLE, DORMITORY

    @Column(name = "hostel_block")
    private String hostelBlock; // BOYS_BLOCK_A, GIRLS_BLOCK_B, etc.

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "occupied")
    private Integer occupied = 0;

    @Column(name = "monthly_rent", precision = 10, scale = 2)
    private java.math.BigDecimal monthlyRent;

    @Column(name = "facilities", columnDefinition = "TEXT")
    private String facilities; // AC, Wi-Fi, Attached Bathroom

    @Column(name = "status")
    private String status = "AVAILABLE"; // AVAILABLE, FULL, UNDER_MAINTENANCE
}
