package com.auction.complaint.service;

import com.auction.audit.facade.AuditLogFacade;
import com.auction.complaint.entity.Complaint;
import com.auction.complaint.entity.Review;
import com.auction.complaint.repository.ComplaintRepository;
import com.auction.complaint.repository.ReviewRepository;
import com.auction.notification.facade.NotificationFacade;
import com.auction.order.entity.Order;
import com.auction.order.entity.OrderStatus;
import com.auction.order.repository.OrderRepository;
import com.auction.wallet.facade.WalletFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ComplaintServiceTest {

    private InMemoryReviewRepo reviewRepository;
    private InMemoryComplaintRepo complaintRepository;
    private InMemoryOrderRepo orderRepository;
    private DummyWalletFacade walletFacade;
    private DummyNotifFacade notificationFacade;
    private DummyAuditFacade auditLogFacade;

    private ComplaintService complaintService;

    private Order order;

    @BeforeEach
    void setUp() {
        reviewRepository = new InMemoryReviewRepo();
        complaintRepository = new InMemoryComplaintRepo();
        orderRepository = new InMemoryOrderRepo();
        walletFacade = new DummyWalletFacade();
        notificationFacade = new DummyNotifFacade();
        auditLogFacade = new DummyAuditFacade();

        complaintService = new ComplaintService(
                reviewRepository,
                complaintRepository,
                orderRepository,
                walletFacade,
                notificationFacade,
                auditLogFacade
        );

        order = new Order();
        order.setId(10L);
        order.setBuyerId(1L);
        order.setSellerId(2L);
        order.setTotalAmount(5_000_000L);
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
    }

    @Test
    @DisplayName("Thêm đánh giá uy tín thành công cho đơn hàng hoàn tất")
    void testAddReview_Success() {
        Review review = complaintService.addReview(order.getId(), 1L, 5, "Sản phẩm chất lượng, giao nhanh!");

        assertNotNull(review.getId());
        assertEquals(5, review.getRating());
        assertEquals(2L, review.getRevieweeId());
        assertEquals("Sản phẩm chất lượng, giao nhanh!", review.getComment());
    }

    @Test
    @DisplayName("Gửi khiếu nại thành công khi có vấn đề đơn hàng")
    void testFileComplaint_Success() {
        Complaint complaint = complaintService.fileComplaint(order.getId(), 1L, "ITEM_NOT_AS_DESCRIBED", "Hàng có vết xước", "https://img.com/x.jpg");

        assertNotNull(complaint.getId());
        assertEquals("PENDING", complaint.getStatus());
        assertEquals("ITEM_NOT_AS_DESCRIBED", complaint.getComplaintType());
    }

    @Test
    @DisplayName("Admin xử lý khiếu nại và hoàn tiền cho người mua")
    void testResolveComplaint_WithRefund() {
        Complaint complaint = complaintService.fileComplaint(order.getId(), 1L, "ITEM_NOT_RECEIVED", "Chưa nhận được hàng", null);

        Complaint resolved = complaintService.resolveComplaint(complaint.getId(), 99L, "Chấp thuận khiếu nại", true);

        assertEquals("RESOLVED", resolved.getStatus());
        assertTrue(walletFacade.refundOrderCalled);
        assertEquals(5_000_000L, walletFacade.lastRefundAmount);
    }

    // --- Doubles ---
    static class DummyWalletFacade implements WalletFacade {
        boolean refundOrderCalled = false;
        long lastRefundAmount = 0L;
        @Override public long getAvailableBalance(Long userId) { return 0; }
        @Override public void createWalletForUser(Long userId) {}
        @Override public void topUp(Long userId, long amount, String idempotencyKey) {}
        @Override public void withdraw(Long userId, long amount, String bankAccount, String idempotencyKey) {}
        @Override public void payOrder(Long buyerId, Long orderId, long amount, String idempotencyKey) {}
        @Override public void payoutSeller(Long sellerId, Long orderId, long finalPrice, double feePercent, String idempotencyKey) {}
        @Override public void refundOrder(Long buyerId, Long orderId, long amount, String idempotencyKey) {
            refundOrderCalled = true;
            lastRefundAmount = amount;
        }
    }

    static class DummyNotifFacade implements NotificationFacade {
        @Override public void sendNotification(Long userId, String type, String title, String content, String actionUrl) {}
        @Override public int getUnreadCount(Long userId) { return 0; }
        @Override public void markAsRead(Long notificationId, Long userId) {}
    }

    static class DummyAuditFacade implements AuditLogFacade {
        @Override public void log(String action, Long userId, String entityName, Long entityId, String details) {}
    }

    static class InMemoryReviewRepo implements ReviewRepository {
        Map<Long, Review> store = new ConcurrentHashMap<>();
        AtomicLong seq = new AtomicLong(1);
        @Override public Optional<Review> findByOrderIdAndReviewerId(Long orderId, Long reviewerId) {
            return store.values().stream().filter(r -> r.getOrderId().equals(orderId) && r.getReviewerId().equals(reviewerId)).findFirst();
        }
        @Override public List<Review> findByRevieweeIdOrderByCreatedAtDesc(Long revieweeId) {
            return store.values().stream().filter(r -> r.getRevieweeId().equals(revieweeId)).toList();
        }
        @Override public <S extends Review> S save(S entity) {
            if (entity.getId() == null) entity.setId(seq.incrementAndGet());
            store.put(entity.getId(), entity);
            return entity;
        }
        @Override public Optional<Review> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(Review entity) { store.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends Review> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<Review> findAll() { return new ArrayList<>(store.values()); }
        @Override public List<Review> findAllById(Iterable<Long> ids) { return Collections.emptyList(); }
        @Override public <S extends Review> List<S> saveAll(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void flush() {}
        @Override public <S extends Review> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Review> List<S> saveAllAndFlush(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void deleteAllInBatch(Iterable<Review> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public Review getOne(Long id) { return store.get(id); }
        @Override public Review getById(Long id) { return store.get(id); }
        @Override public Review getReferenceById(Long id) { return store.get(id); }
        @Override public <S extends Review> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends Review> List<S> findAll(Example<S> example) { return Collections.emptyList(); }
        @Override public <S extends Review> List<S> findAll(Example<S> example, Sort sort) { return Collections.emptyList(); }
        @Override public <S extends Review> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends Review> long count(Example<S> example) { return 0; }
        @Override public <S extends Review> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Review, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<Review> findAll(Sort sort) { return Collections.emptyList(); }
        @Override public Page<Review> findAll(Pageable pageable) { return null; }
    }

    static class InMemoryComplaintRepo implements ComplaintRepository {
        Map<Long, Complaint> store = new ConcurrentHashMap<>();
        AtomicLong seq = new AtomicLong(1);
        @Override public List<Complaint> findByOrderIdOrderByCreatedAtDesc(Long orderId) {
            return store.values().stream().filter(c -> c.getOrderId().equals(orderId)).toList();
        }
        @Override public List<Complaint> findByComplainantIdOrderByCreatedAtDesc(Long complainantId) {
            return store.values().stream().filter(c -> c.getComplainantId().equals(complainantId)).toList();
        }
        @Override public List<Complaint> findByStatusOrderByCreatedAtDesc(String status) {
            return store.values().stream().filter(c -> c.getStatus().equals(status)).toList();
        }
        @Override public <S extends Complaint> S save(S entity) {
            if (entity.getId() == null) entity.setId(seq.incrementAndGet());
            store.put(entity.getId(), entity);
            return entity;
        }
        @Override public Optional<Complaint> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(Complaint entity) { store.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends Complaint> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<Complaint> findAll() { return new ArrayList<>(store.values()); }
        @Override public List<Complaint> findAllById(Iterable<Long> ids) { return Collections.emptyList(); }
        @Override public <S extends Complaint> List<S> saveAll(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void flush() {}
        @Override public <S extends Complaint> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Complaint> List<S> saveAllAndFlush(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void deleteAllInBatch(Iterable<Complaint> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public Complaint getOne(Long id) { return store.get(id); }
        @Override public Complaint getById(Long id) { return store.get(id); }
        @Override public Complaint getReferenceById(Long id) { return store.get(id); }
        @Override public <S extends Complaint> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends Complaint> List<S> findAll(Example<S> example) { return Collections.emptyList(); }
        @Override public <S extends Complaint> List<S> findAll(Example<S> example, Sort sort) { return Collections.emptyList(); }
        @Override public <S extends Complaint> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends Complaint> long count(Example<S> example) { return 0; }
        @Override public <S extends Complaint> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Complaint, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<Complaint> findAll(Sort sort) { return Collections.emptyList(); }
        @Override public Page<Complaint> findAll(Pageable pageable) { return null; }
    }

    static class InMemoryOrderRepo implements OrderRepository {
        Map<Long, Order> store = new ConcurrentHashMap<>();
        @Override public Optional<Order> findByIdForUpdate(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public Optional<Order> findByAuctionId(Long auctionId) { return Optional.empty(); }
        @Override public List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId) { return Collections.emptyList(); }
        @Override public List<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId) { return Collections.emptyList(); }
        @Override public List<Order> findByStatusAndPaymentDeadlineBefore(OrderStatus status, LocalDateTime deadline) { return Collections.emptyList(); }
        @Override public <S extends Order> S save(S entity) { store.put(entity.getId(), entity); return entity; }
        @Override public Optional<Order> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(Order entity) { store.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends Order> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<Order> findAll() { return new ArrayList<>(store.values()); }
        @Override public List<Order> findAllById(Iterable<Long> ids) { return Collections.emptyList(); }
        @Override public <S extends Order> List<S> saveAll(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void flush() {}
        @Override public <S extends Order> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Order> List<S> saveAllAndFlush(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void deleteAllInBatch(Iterable<Order> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public Order getOne(Long id) { return store.get(id); }
        @Override public Order getById(Long id) { return store.get(id); }
        @Override public Order getReferenceById(Long id) { return store.get(id); }
        @Override public <S extends Order> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends Order> List<S> findAll(Example<S> example) { return Collections.emptyList(); }
        @Override public <S extends Order> List<S> findAll(Example<S> example, Sort sort) { return Collections.emptyList(); }
        @Override public <S extends Order> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends Order> long count(Example<S> example) { return 0; }
        @Override public <S extends Order> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Order, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<Order> findAll(Sort sort) { return Collections.emptyList(); }
        @Override public Page<Order> findAll(Pageable pageable) { return null; }
    }
}

