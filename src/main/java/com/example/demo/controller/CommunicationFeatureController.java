package com.example.demo.controller;

import com.example.demo.dto.communication.EmailPayloadResponse;
import com.example.demo.model.Event;
import com.example.demo.model.EventRegistration;
import com.example.demo.model.Notification;
import com.example.demo.model.Notice;
import com.example.demo.service.CommunicationFeatureService;
import com.example.demo.service.EventService;
import com.example.demo.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communication")
@CrossOrigin(origins = "*")
public class CommunicationFeatureController {

    @Autowired private CommunicationFeatureService communicationFeatureService;
    @Autowired private NoticeService noticeService;
    @Autowired private EventService eventService;

    @PostMapping("/events/register")
    public ResponseEntity<EventRegistration> register(@RequestBody EventRegistration registration) {
        return ResponseEntity.status(HttpStatus.CREATED).body(communicationFeatureService.registerForEvent(registration));
    }

    @GetMapping("/events/{eventId}/registrations")
    public ResponseEntity<Page<EventRegistration>> getRegistrations(@PathVariable Long eventId,
                                                                    @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(communicationFeatureService.getRegistrationsForEvent(eventId, pageable));
    }

    @PostMapping("/notifications")
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        return ResponseEntity.status(HttpStatus.CREATED).body(communicationFeatureService.createNotification(notification));
    }

    @GetMapping("/notifications/user/{userId}")
    public ResponseEntity<Page<Notification>> getNotifications(@PathVariable Long userId,
                                                               @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(communicationFeatureService.getNotificationsForUser(userId, pageable));
    }

    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Notification> markRead(@PathVariable Long notificationId) {
        return ResponseEntity.ok(communicationFeatureService.markRead(notificationId));
    }

    @GetMapping("/notices/{noticeId}/email-payload")
    public ResponseEntity<EmailPayloadResponse> getNoticeEmailPayload(@PathVariable Long noticeId) {
        Notice notice = noticeService.getById(noticeId);
        return ResponseEntity.ok(communicationFeatureService.buildNoticeEmailPayload(notice));
    }

    @GetMapping("/events/{eventId}/email-payload")
    public ResponseEntity<EmailPayloadResponse> getEventEmailPayload(@PathVariable Long eventId) {
        Event event = eventService.getEventById(eventId);
        return ResponseEntity.ok(communicationFeatureService.buildEventEmailPayload(event));
    }
}
