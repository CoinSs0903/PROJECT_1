package com.auction.user.controller;

import com.auction.common.dto.ApiResponse;
import com.auction.user.dto.RegisterRequest;
import com.auction.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Bạn đã đăng xuất thành công.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.register(request);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("registerRequest", request);
            return "redirect:/register";
        }
    }

    @PostMapping("/api/auth/register")
    @ResponseBody
    public ApiResponse<String> apiRegister(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ApiResponse.ok("Đăng ký thành công", request.getUsername());
    }
}

