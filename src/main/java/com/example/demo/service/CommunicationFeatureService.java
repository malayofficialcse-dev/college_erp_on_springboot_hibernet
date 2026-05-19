package com.example.demo.service;

import com.example.demo.dto.communication.EmailPayloadResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Event;
import com.example.demo.model.EventRegistration;
import com.example.demo.model.Notification;
import com.example.demo.model.Notice;
import com.example.demo.model.User;
import com.example.demo.repository.EventRegistrationRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommunicationFeatureService {

    @Autowired private EventRepository eventRepository;
    @Autowired private EventRegistrationRepository eventRegistrationRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public EventRegistration registerForEvent(EventRegistration registration) {
        Event event = eventRepository.findById(registration.getEvent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", registration.getEvent().getId()));
        registration.setEvent(event);
        registration.setRegisteredAt(LocalDateTime.now());
        return eventRegistrationRepository.save(registration);
    }

    public Page<EventRegistration> getRegistrationsForEvent(Long eventId, Pageable pageable) {
        return eventRegistrationRepository.findByEventId(eventId, pageable);
    }

    @Transactional
    public Notification createNotification(Notification notification) {
        notification.setSentAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public Page<Notification> getNotificationsForUser(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId, pageable);
    }

    @Transactional
    public Notification markRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public EmailPayloadResponse buildNoticeEmailPayload(Notice notice) {
        List<String> recipients = resolveRecipients(notice.getTargetAudience(),
                notice.getDepartment() != null ? notice.getDepartment().getId() : null);
        return new EmailPayloadResponse(notice.getTitle(), notice.getContent(), recipients, notice.getTargetAudience());
    }

    public EmailPayloadResponse buildEventEmailPayload(Event event) {
        List<String> recipients = resolveRecipients("ALL", null);
        String body = "Event: " + event.getTitle() + "\nDate: " + event.getEventDate() + "\nVenue: " + event.getVenue()
                + "\n\n" + event.getDescription();
        return new EmailPayloadResponse("Upcoming Event: " + event.getTitle(), body, recipients, "ALL");
    }

    private List<String> resolveRecipients(String audience, Long departmentId) {
        return userRepository.findAll().stream()
                .filter(user -> matchesAudience(user, audience))
                .map(User::getEmail)
                .toList();
    }

    private boolean matchesAudience(User user, String audience) {
        if (audience == null || "ALL".equalsIgnoreCase(audience)) {
            return true;
        }
        return user.getRoles().stream().anyMatch(role -> role.name().contains(audience.toUpperCase()));
    }
}
