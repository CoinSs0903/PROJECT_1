package com.auction.user.service;

import com.auction.common.exception.AppException;
import com.auction.common.exception.ErrorCode;
import com.auction.user.dto.RegisterRequest;
import com.auction.user.entity.User;
import com.auction.user.entity.UserStatus;
import com.auction.user.repository.UserRepository;
import com.auction.wallet.facade.WalletFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private InMemoryUserRepository userRepository;
    private DummyPasswordEncoder passwordEncoder;
    private DummyWalletFacade walletFacade;
    private UserService userService;
    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        passwordEncoder = new DummyPasswordEncoder();
        walletFacade = new DummyWalletFacade();
        userService = new UserService(userRepository, passwordEncoder, walletFacade);

        request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@gmail.com");
        request.setPassword("password123");
        request.setFullName("Nguyễn Văn A");
    }

    @Test
    @DisplayName("Đăng ký tài khoản thành công và tạo ví tương ứng")
    void testRegisterSuccess() {
        User user = userService.register(request);

        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("encoded_password123", user.getPasswordHash());
        assertEquals(user.getId(), walletFacade.createdWalletUserId);
    }

    @Test
    @DisplayName("Đăng ký thất bại khi trùng tên đăng nhập")
    void testRegisterDuplicateUsername() {
        userService.register(request);

        RegisterRequest duplicate = new RegisterRequest();
        duplicate.setUsername("testuser");
        duplicate.setEmail("other@gmail.com");
        duplicate.setPassword("pass123");
        duplicate.setFullName("Người khác");

        AppException ex = assertThrows(AppException.class, () -> userService.register(duplicate));
        assertEquals(ErrorCode.USERNAME_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    @DisplayName("Ghi nhận gậy phạt vi phạm bùng hàng và cấm đấu giá khi đạt 3 gậy")
    void testRecordUnpaidStrike() {
        User user = userService.register(request);
        assertEquals(0, user.getUnpaidStrikesCount());
        assertEquals(UserStatus.ACTIVE, user.getStatus());

        userService.recordUnpaidStrike(user.getId());
        assertEquals(1, userService.findById(user.getId()).getUnpaidStrikesCount());

        userService.recordUnpaidStrike(user.getId());
        assertEquals(2, userService.findById(user.getId()).getUnpaidStrikesCount());

        userService.recordUnpaidStrike(user.getId());
        User strikeUser = userService.findById(user.getId());
        assertEquals(3, strikeUser.getUnpaidStrikesCount());
        assertEquals(UserStatus.RESTRICTED_BIDDING, strikeUser.getStatus());
    }

    // --- Fake implementations for unit testing ---
    private static class DummyPasswordEncoder implements PasswordEncoder {
        @Override
        public String encode(CharSequence rawPassword) {
            return "encoded_" + rawPassword;
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return ("encoded_" + rawPassword).equals(encodedPassword);
        }
    }

    private static class DummyWalletFacade implements WalletFacade {
        Long createdWalletUserId;

        @Override
        public long getAvailableBalance(Long userId) { return 0; }
        @Override
        public void createWalletForUser(Long userId) { this.createdWalletUserId = userId; }
        @Override
        public void topUp(Long userId, long amount, String idempotencyKey) {}
        @Override
        public void withdraw(Long userId, long amount, String bankAccount, String idempotencyKey) {}
        @Override
        public void payOrder(Long buyerId, Long orderId, long amount, String idempotencyKey) {}
        @Override
        public void payoutSeller(Long sellerId, Long orderId, long finalPrice, double feePercent, String idempotencyKey) {}
        @Override
        public void refundOrder(Long buyerId, Long orderId, long amount, String idempotencyKey) {}
    }

    private static class InMemoryUserRepository implements UserRepository {
        private final Map<Long, User> db = new ConcurrentHashMap<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public Optional<User> findByUsername(String username) {
            return db.values().stream().filter(u -> u.getUsername().equals(username)).findFirst();
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return db.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
        }

        @Override
        public boolean existsByUsername(String username) {
            return db.values().stream().anyMatch(u -> u.getUsername().equals(username));
        }

        @Override
        public boolean existsByEmail(String email) {
            return db.values().stream().anyMatch(u -> u.getEmail().equals(email));
        }

        @Override
        public <S extends User> S save(S entity) {
            if (entity.getId() == null) {
                entity.setId(idGen.getAndIncrement());
            }
            db.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Optional<User> findById(Long aLong) {
            return Optional.ofNullable(db.get(aLong));
        }

        @Override public boolean existsById(Long aLong) { return db.containsKey(aLong); }
        @Override public List<User> findAll() { return new ArrayList<>(db.values()); }
        @Override public List<User> findAllById(Iterable<Long> longs) { return null; }
        @Override public long count() { return db.size(); }
        @Override public void deleteById(Long aLong) { db.remove(aLong); }
        @Override public void delete(User entity) { db.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> longs) {}
        @Override public void deleteAll(Iterable<? extends User> entities) {}
        @Override public void deleteAll() { db.clear(); }
        @Override public void flush() {}
        @Override public <S extends User> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<User> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}
        @Override public void deleteAllInBatch() {}
        @Override public User getOne(Long aLong) { return db.get(aLong); }
        @Override public User getById(Long aLong) { return db.get(aLong); }
        @Override public User getReferenceById(Long aLong) { return db.get(aLong); }
        @Override public <S extends User> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends User> List<S> findAll(Example<S> example) { return null; }
        @Override public <S extends User> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends User> long count(Example<S> example) { return 0; }
        @Override public <S extends User> boolean exists(Example<S> example) { return false; }
        @Override public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public <S extends User> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public List<User> findAll(Sort sort) { return null; }
        @Override public Page<User> findAll(Pageable pageable) { return null; }
    }
}
