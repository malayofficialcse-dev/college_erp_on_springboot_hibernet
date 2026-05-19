package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_type")
    private String eventType; // CULTURAL, SPORTS, ACADEMIC, WORKSHOP, SEMINAR

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "venue")
    private String venue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organized_by_dept")
    private Department organizedByDept;

    @Column(name = "organizer_name")
    private String organizerName;

    @Column(name = "status")
    private String status = "UPCOMING"; // UPCOMING, ONGOING, COMPLETED, CANCELLED
}
