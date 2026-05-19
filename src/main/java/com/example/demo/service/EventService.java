package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Event;
import com.example.demo.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    @Autowired private EventRepository eventRepository;

    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
    }

    public Page<Event> getEventsByStatus(String status, Pageable pageable) {
        return eventRepository.findByStatus(status, pageable);
    }

    public Page<Event> getEventsByType(String eventType, Pageable pageable) {
        return eventRepository.findByEventType(eventType, pageable);
    }

    @Transactional
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(Long id, Event details) {
        Event event = getEventById(id);
        event.setTitle(details.getTitle());
        event.setDescription(details.getDescription());
        event.setEventType(details.getEventType());
        event.setEventDate(details.getEventDate());
        event.setStartTime(details.getStartTime());
        event.setEndTime(details.getEndTime());
        event.setVenue(details.getVenue());
        event.setOrganizedByDept(details.getOrganizedByDept());
        event.setOrganizerName(details.getOrganizerName());
        event.setStatus(details.getStatus());
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        getEventById(id);
        eventRepository.deleteById(id);
    }
}
