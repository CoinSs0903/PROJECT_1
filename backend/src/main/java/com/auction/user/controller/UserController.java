package com.auction.user.controller;

import com.auction.common.dto.ApiResponse;
import com.auction.user.dto.UserProfileDto;
import com.auction.user.entity.User;
import com.auction.user.security.CustomUserDetails;
import com.auction.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userService.findById(userDetails.getId());
        model.addAttribute("userProfile", UserProfileDto.fromEntity(user));
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @ModelAttribute UserProfileDto dto,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(userDetails.getId(), dto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @GetMapping("/api/user/me")
    @ResponseBody
    public ApiResponse<UserProfileDto> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getId());
        return ApiResponse.ok(UserProfileDto.fromEntity(user));
    }
}

