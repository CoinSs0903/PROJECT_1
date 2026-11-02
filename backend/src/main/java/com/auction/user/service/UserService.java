package com.auction.user.service;

import com.auction.common.exception.AppException;
import com.auction.common.exception.ErrorCode;
import com.auction.user.dto.RegisterRequest;
import com.auction.user.dto.UserProfileDto;
import com.auction.user.entity.User;
import com.auction.user.entity.UserRole;
import com.auction.user.entity.UserStatus;
import com.auction.user.repository.UserRepository;
import com.auction.wallet.facade.WalletFacade;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletFacade walletFacade;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       @Lazy WalletFacade walletFacade) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletFacade = walletFacade;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setShippingAddress(request.getShippingAddress());
        user.setBankName(request.getBankName());
        user.setBankAccountNumber(request.getBankAccountNumber());
        user.setRole(UserRole.ROLE_USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setUnpaidStrikesCount(0);

        User savedUser = userRepository.save(user);

        // Khởi tạo ví cá nhân cho người dùng mới
        try {
            walletFacade.createWalletForUser(savedUser.getId());
        } catch (Exception ignored) {
            // Trường hợp module wallet chưa kích hoạt
        }

        return savedUser;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public User updateProfile(Long userId, UserProfileDto dto) {
        User user = findById(userId);
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setShippingAddress(dto.getShippingAddress());
        user.setBankName(dto.getBankName());
        user.setBankAccountNumber(dto.getBankAccountNumber());
        return userRepository.save(user);
    }

    @Transactional
    public void recordUnpaidStrike(Long userId) {
        User user = findById(userId);
        user.setUnpaidStrikesCount(user.getUnpaidStrikesCount() + 1);
        if (user.getUnpaidStrikesCount() >= 3) {
            user.setStatus(UserStatus.RESTRICTED_BIDDING);
        }
        userRepository.save(user);
    }
}

