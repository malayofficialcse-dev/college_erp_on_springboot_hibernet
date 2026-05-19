package com.example.demo.repository;

import com.example.demo.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdOrderBySentAtDesc(Long userId, Pageable pageable);
    Page<Notification> findByTargetAudienceOrderBySentAtDesc(String targetAudience, Pageable pageable);
}
