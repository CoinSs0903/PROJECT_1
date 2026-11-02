package com.auction.common.exception;

import com.auction.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/") || "application/json".equals(request.getHeader("Accept"));
    }

    @ExceptionHandler(AppException.class)
    public Object handleAppException(AppException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.BAD_REQUEST, "Dữ liệu nhập không hợp lệ: " + errors));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception ex, HttpServletRequest request, Model model) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage()));
        }
        model.addAttribute("errorMessage", "Đã có lỗi xảy ra: " + ex.getMessage());
        return "error";
    }
}

