package com.auction.notification.service;

import com.auction.notification.entity.Notification;
import com.auction.notification.entity.NotificationType;
import com.auction.notification.facade.NotificationFacade;
import com.auction.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService implements NotificationFacade {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void sendNotification(Long userId, String type, String title, String content, String actionUrl) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        try {
            notification.setNotificationType(NotificationType.valueOf(type));
        } catch (Exception e) {
            notification.setNotificationType(NotificationType.GENERAL);
        }
        notification.setTitle(title);
        notification.setContent(content);
        notification.setActionUrl(actionUrl);
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUserId().equals(userId)) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}

