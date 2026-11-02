package com.auction.notification.facade;

public interface NotificationFacade {
    void sendNotification(Long userId, String type, String title, String content, String actionUrl);
    int getUnreadCount(Long userId);
    void markAsRead(Long notificationId, Long userId);
}

