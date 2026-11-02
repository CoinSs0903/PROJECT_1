package com.auction.user.dto;

import com.auction.user.entity.User;
import com.auction.user.entity.UserStatus;

public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String shippingAddress;
    private String bankName;
    private String bankAccountNumber;
    private String role;
    private int unpaidStrikesCount;
    private UserStatus status;

    public UserProfileDto() {}

    public static UserProfileDto fromEntity(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setShippingAddress(user.getShippingAddress());
        dto.setBankName(user.getBankName());
        dto.setBankAccountNumber(user.getBankAccountNumber());
        dto.setRole(user.getRole().name());
        dto.setUnpaidStrikesCount(user.getUnpaidStrikesCount());
        dto.setStatus(user.getStatus());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getUnpaidStrikesCount() {
        return unpaidStrikesCount;
    }

    public void setUnpaidStrikesCount(int unpaidStrikesCount) {
        this.unpaidStrikesCount = unpaidStrikesCount;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}

