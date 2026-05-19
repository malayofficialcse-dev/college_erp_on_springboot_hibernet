package com.example.demo.controller;

import com.example.demo.model.Event;
import com.example.demo.service.EventService;
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
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired private EventService eventService;

    @GetMapping
    public ResponseEntity<Page<Event>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(eventService.getAllEvents(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Event>> getByStatus(@PathVariable String status,
                                                    @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(eventService.getEventsByStatus(status, pageable));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Page<Event>> getByType(@PathVariable String type,
                                                  @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(eventService.getEventsByType(type, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Event> create(@Valid @RequestBody Event event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(event));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Event> update(@PathVariable Long id, @RequestBody Event details) {
        return ResponseEntity.ok(eventService.updateEvent(id, details));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
