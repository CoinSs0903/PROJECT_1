package com.auction.notification.controller;

import com.auction.common.dto.ApiResponse;
import com.auction.notification.entity.Notification;
import com.auction.notification.service.NotificationService;
import com.auction.user.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public String viewNotifications(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<Notification> list = notificationService.getAllNotifications(userDetails.getId());
        model.addAttribute("notifications", list);
        return "notification/index";
    }

    @GetMapping("/api/notifications/unread")
    @ResponseBody
    public ApiResponse<List<Notification>> getUnreadNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ApiResponse.ok(List.of());
        }
        return ApiResponse.ok(notificationService.getUnreadNotifications(userDetails.getId()));
    }

    @PostMapping("/api/notifications/{id}/read")
    @ResponseBody
    public ApiResponse<Void> markAsRead(@PathVariable("id") Long id,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAsRead(id, userDetails.getId());
        return ApiResponse.ok(null);
    }
}

