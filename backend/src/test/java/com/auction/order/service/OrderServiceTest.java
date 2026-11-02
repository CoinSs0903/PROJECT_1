package com.auction.order.service;

import com.auction.auction.entity.Auction;
import com.auction.auction.entity.AuctionStatus;
import com.auction.auction.repository.AuctionRepository;
import com.auction.audit.facade.AuditLogFacade;
import com.auction.catalog.entity.Item;
import com.auction.catalog.repository.ItemRepository;
import com.auction.notification.facade.NotificationFacade;
import com.auction.order.entity.Order;
import com.auction.order.entity.OrderStatus;
import com.auction.order.entity.PurchaseType;
import com.auction.order.repository.OrderRepository;
import com.auction.user.entity.User;
import com.auction.user.entity.UserStatus;
import com.auction.user.repository.UserRepository;
import com.auction.user.service.UserService;
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

class OrderServiceTest {

    private InMemoryOrderRepo orderRepository;
    private InMemoryAuctionRepo auctionRepository;
    private InMemoryItemRepo itemRepository;
    private InMemoryUserRepo userRepository;
    private DummyWalletFacade walletFacade;
    private DummyUserService userService;
    private DummyNotifFacade notificationFacade;
    private DummyAuditFacade auditLogFacade;

    private OrderService orderService;

    private User buyer;
    private User seller;
    private Item item;
    private Auction auction;
    private Order order;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepo();
        auctionRepository = new InMemoryAuctionRepo();
        itemRepository = new InMemoryItemRepo();
        userRepository = new InMemoryUserRepo();
        walletFacade = new DummyWalletFacade();
        userService = new DummyUserService();
        notificationFacade = new DummyNotifFacade();
        auditLogFacade = new DummyAuditFacade();

        orderService = new OrderService(
                orderRepository,
                auctionRepository,
                itemRepository,
                userRepository,
                walletFacade,
                userService,
                notificationFacade,
                auditLogFacade
        );

        buyer = new User();
        buyer.setId(10L);
        buyer.setUsername("buyer10");
        buyer.setStatus(UserStatus.ACTIVE);
        userRepository.save(buyer);

        seller = new User();
        seller.setId(20L);
        seller.setUsername("seller20");
        seller.setStatus(UserStatus.ACTIVE);
        userRepository.save(seller);

        item = new Item();
        item.setId(100L);
        item.setSellerId(seller.getId());
        item.setTitle("Laptop Dell XPS 15");
        itemRepository.save(item);

        auction = new Auction();
        auction.setId(200L);
        auction.setItemId(item.getId());
        auction.setStartingPrice(20_000_000L);
        auction.setCurrentPrice(25_000_000L);
        auction.setStatus(AuctionStatus.ENDED_SOLD);
        auctionRepository.save(auction);

        order = new Order();
        order.setId(1L);
        order.setAuctionId(auction.getId());
        order.setBuyerId(buyer.getId());
        order.setSellerId(seller.getId());
        order.setPurchaseType(PurchaseType.AUCTION_WIN);
        order.setFinalPrice(25_000_000L);
        order.setShippingFee(0L);
        order.setTotalAmount(25_000_000L);
        order.setStatus(OrderStatus.AWAITING_PAYMENT);
        order.setPaymentDeadline(LocalDateTime.now().plusHours(72));
        orderRepository.save(order);
    }

    @Test
    @DisplayName("Thanh toán đơn hàng thành công qua ví bảo chứng: Trạng thái chuyển sang PAID")
    void testCheckoutAndPay_Success() {
        Order paid = orderService.checkoutAndPay(order.getId(), buyer.getId(), "Số 123 Đường Cầu Giấy, Hà Nội");

        assertEquals(OrderStatus.PAID, paid.getStatus());
        assertEquals("Số 123 Đường Cầu Giấy, Hà Nội", paid.getShippingAddress());
        assertTrue(walletFacade.payOrderCalled);
        assertEquals(25_000_000L, walletFacade.lastPaidAmount);

        // Notify seller
        assertTrue(notificationFacade.notifications.stream().anyMatch(n -> n.contains("ORDER_PAID:20")));
    }

    @Test
    @DisplayName("Người bán cập nhật mã vận đơn thành công: Trạng thái chuyển sang SHIPPED")
    void testShipOrder_Success() {
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        Order shipped = orderService.shipOrder(order.getId(), seller.getId(), "VNPOST-999888");

        assertEquals(OrderStatus.SHIPPED, shipped.getStatus());
        assertEquals("VNPOST-999888", shipped.getTrackingNumber());
    }

    @Test
    @DisplayName("Người mua xác nhận nhận hàng: Đơn chuyển COMPLETED và giải ngân 95% cho người bán (5% phí sàn)")
    void testConfirmReceived_SuccessAndPayout() {
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);

        Order completed = orderService.confirmReceived(order.getId(), buyer.getId());

        assertEquals(OrderStatus.COMPLETED, completed.getStatus());
        assertTrue(walletFacade.payoutSellerCalled);
        assertEquals(0.05, walletFacade.lastFeePercent);
    }

    @Test
    @DisplayName("Quá hạn thanh toán 72 giờ: Huỷ đơn CANCELLED_UNPAID và phạt 1 gậy bùng hàng cho Buyer")
    void testCancelUnpaidOrder_PenalizesBuyer() {
        orderService.cancelUnpaidOrder(order.getId());

        Order cancelled = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.CANCELLED_UNPAID, cancelled.getStatus());
        assertTrue(userService.strikeRecorded);
        assertEquals(buyer.getId(), userService.lastStrikedUserId);
    }

    @Test
    @DisplayName("Mở bán lại phiên đấu giá (Relist): Tạo phiên mới từ chính sản phẩm đó")
    void testRelistAuction_Success() {
        Auction relisted = orderService.relistAuction(auction.getId(), seller.getId());

        assertNotNull(relisted);
        assertEquals(item.getId(), relisted.getItemId());
        assertEquals(AuctionStatus.ACTIVE, relisted.getStatus());
        assertEquals(0, relisted.getBidCount());
        assertEquals(auction.getStartingPrice(), relisted.getCurrentPrice());
    }

    // --- Doubles ---
    static class DummyWalletFacade implements WalletFacade {
        boolean payOrderCalled = false;
        long lastPaidAmount = 0L;
        boolean payoutSellerCalled = false;
        double lastFeePercent = 0.0;

        @Override public long getAvailableBalance(Long userId) { return 100_000_000L; }
        @Override public void createWalletForUser(Long userId) {}
        @Override public void topUp(Long userId, long amount, String idempotencyKey) {}
        @Override public void withdraw(Long userId, long amount, String bankAccount, String idempotencyKey) {}
        @Override public void payOrder(Long buyerId, Long orderId, long amount, String idempotencyKey) {
            payOrderCalled = true;
            lastPaidAmount = amount;
        }
        @Override public void payoutSeller(Long sellerId, Long orderId, long finalPrice, double feePercent, String idempotencyKey) {
            payoutSellerCalled = true;
            lastFeePercent = feePercent;
        }
        @Override public void refundOrder(Long buyerId, Long orderId, long amount, String idempotencyKey) {}
    }

    static class DummyUserService extends UserService {
        boolean strikeRecorded = false;
        Long lastStrikedUserId = null;
        public DummyUserService() { super(null, null, null); }
        @Override public void recordUnpaidStrike(Long userId) {
            strikeRecorded = true;
            lastStrikedUserId = userId;
        }
    }

    static class DummyNotifFacade implements NotificationFacade {
        List<String> notifications = new ArrayList<>();
        @Override public void sendNotification(Long userId, String type, String title, String content, String url) {
            notifications.add(type + ":" + userId);
        }
        @Override public int getUnreadCount(Long userId) { return 0; }
        @Override public void markAsRead(Long notificationId, Long userId) {}
    }

    static class DummyAuditFacade implements AuditLogFacade {
        @Override public void log(String action, Long userId, String entityName, Long entityId, String details) {}
    }

    static class InMemoryOrderRepo implements OrderRepository {
        Map<Long, Order> store = new ConcurrentHashMap<>();
        AtomicLong seq = new AtomicLong(10);
        @Override public Optional<Order> findByIdForUpdate(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public Optional<Order> findByAuctionId(Long auctionId) {
            return store.values().stream().filter(o -> o.getAuctionId().equals(auctionId)).findFirst();
        }
        @Override public List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId) {
            return store.values().stream().filter(o -> o.getBuyerId().equals(buyerId)).toList();
        }
        @Override public List<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId) {
            return store.values().stream().filter(o -> o.getSellerId().equals(sellerId)).toList();
        }
        @Override public List<Order> findByStatusAndPaymentDeadlineBefore(OrderStatus status, LocalDateTime deadline) {
            return store.values().stream().filter(o -> o.getStatus() == status && o.getPaymentDeadline().isBefore(deadline)).toList();
        }
        @Override public <S extends Order> S save(S entity) {
            if (entity.getId() == null) entity.setId(seq.incrementAndGet());
            store.put(entity.getId(), entity);
            return entity;
        }
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

    static class InMemoryAuctionRepo implements AuctionRepository {
        Map<Long, Auction> store = new ConcurrentHashMap<>();
        AtomicLong seq = new AtomicLong(100);
        @Override public Optional<Auction> findByIdForUpdate(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public List<Auction> findByStatusOrderByCreatedAtDesc(AuctionStatus status) { return Collections.emptyList(); }
        @Override public List<Auction> findByItemIdOrderByCreatedAtDesc(Long itemId) { return Collections.emptyList(); }
        @Override public List<Auction> findExpiredActiveAuctions(LocalDateTime now) { return Collections.emptyList(); }
        @Override public List<Auction> findDueScheduledAuctions(LocalDateTime now) { return Collections.emptyList(); }
        @Override public <S extends Auction> S save(S entity) {
            if (entity.getId() == null) entity.setId(seq.incrementAndGet());
            store.put(entity.getId(), entity);
            return entity;
        }
        @Override public Optional<Auction> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(Auction entity) { store.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends Auction> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<Auction> findAll() { return new ArrayList<>(store.values()); }
        @Override public List<Auction> findAllById(Iterable<Long> ids) { return Collections.emptyList(); }
        @Override public <S extends Auction> List<S> saveAll(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void flush() {}
        @Override public <S extends Auction> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Auction> List<S> saveAllAndFlush(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void deleteAllInBatch(Iterable<Auction> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public Auction getOne(Long id) { return store.get(id); }
        @Override public Auction getById(Long id) { return store.get(id); }
        @Override public Auction getReferenceById(Long id) { return store.get(id); }
        @Override public <S extends Auction> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends Auction> List<S> findAll(Example<S> example) { return Collections.emptyList(); }
        @Override public <S extends Auction> List<S> findAll(Example<S> example, Sort sort) { return Collections.emptyList(); }
        @Override public <S extends Auction> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends Auction> long count(Example<S> example) { return 0; }
        @Override public <S extends Auction> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Auction, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<Auction> findAll(Sort sort) { return Collections.emptyList(); }
        @Override public Page<Auction> findAll(Pageable pageable) { return null; }
    }

    static class InMemoryItemRepo implements ItemRepository {
        Map<Long, Item> store = new ConcurrentHashMap<>();
        @Override public List<Item> findBySellerIdOrderByCreatedAtDesc(Long sellerId) { return Collections.emptyList(); }
        @Override public List<Item> findByCategoryIdOrderByCreatedAtDesc(Long categoryId) { return Collections.emptyList(); }
        @Override public <S extends Item> S save(S entity) { store.put(entity.getId(), entity); return entity; }
        @Override public Optional<Item> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(Item entity) { store.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends Item> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<Item> findAll() { return new ArrayList<>(store.values()); }
        @Override public List<Item> findAllById(Iterable<Long> ids) { return Collections.emptyList(); }
        @Override public <S extends Item> List<S> saveAll(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void flush() {}
        @Override public <S extends Item> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Item> List<S> saveAllAndFlush(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void deleteAllInBatch(Iterable<Item> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public Item getOne(Long id) { return store.get(id); }
        @Override public Item getById(Long id) { return store.get(id); }
        @Override public Item getReferenceById(Long id) { return store.get(id); }
        @Override public <S extends Item> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends Item> List<S> findAll(Example<S> example) { return Collections.emptyList(); }
        @Override public <S extends Item> List<S> findAll(Example<S> example, Sort sort) { return Collections.emptyList(); }
        @Override public <S extends Item> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends Item> long count(Example<S> example) { return 0; }
        @Override public <S extends Item> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Item, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<Item> findAll(Sort sort) { return Collections.emptyList(); }
        @Override public Page<Item> findAll(Pageable pageable) { return null; }
    }

    static class InMemoryUserRepo implements UserRepository {
        Map<Long, User> store = new ConcurrentHashMap<>();
        @Override public Optional<User> findByUsername(String username) { return store.values().stream().filter(u -> u.getUsername().equals(username)).findFirst(); }
        @Override public Optional<User> findByEmail(String email) { return store.values().stream().filter(u -> u.getEmail().equals(email)).findFirst(); }
        @Override public boolean existsByUsername(String username) { return store.values().stream().anyMatch(u -> u.getUsername().equals(username)); }
        @Override public boolean existsByEmail(String email) { return store.values().stream().anyMatch(u -> u.getEmail().equals(email)); }
        @Override public <S extends User> S save(S entity) { store.put(entity.getId(), entity); return entity; }
        @Override public Optional<User> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(User entity) { store.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends User> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<User> findAll() { return new ArrayList<>(store.values()); }
        @Override public List<User> findAllById(Iterable<Long> ids) { return Collections.emptyList(); }
        @Override public <S extends User> List<S> saveAll(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void flush() {}
        @Override public <S extends User> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void deleteAllInBatch(Iterable<User> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public User getOne(Long id) { return store.get(id); }
        @Override public User getById(Long id) { return store.get(id); }
        @Override public User getReferenceById(Long id) { return store.get(id); }
        @Override public <S extends User> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends User> List<S> findAll(Example<S> example) { return Collections.emptyList(); }
        @Override public <S extends User> List<S> findAll(Example<S> example, Sort sort) { return Collections.emptyList(); }
        @Override public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends User> long count(Example<S> example) { return 0; }
        @Override public <S extends User> boolean exists(Example<S> example) { return false; }
        @Override public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<User> findAll(Sort sort) { return Collections.emptyList(); }
        @Override public Page<User> findAll(Pageable pageable) { return null; }
    }
}
