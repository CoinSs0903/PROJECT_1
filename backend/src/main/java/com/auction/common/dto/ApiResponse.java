package com.auction.common.dto;

import com.auction.common.exception.ErrorCode;
import java.time.LocalDateTime;

public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setCode(ErrorCode.SUCCESS.getCode());
        response.setMessage(ErrorCode.SUCCESS.getDefaultMessage());
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setCode(ErrorCode.SUCCESS.getCode());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getDefaultMessage());
        return response;
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode(errorCode.getCode());
        response.setMessage(customMessage);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

