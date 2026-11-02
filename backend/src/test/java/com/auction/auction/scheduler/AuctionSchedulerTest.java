package com.auction.auction.scheduler;

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
import com.auction.order.service.OrderService;
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

class AuctionSchedulerTest {

    private InMemoryAuctionRepo auctionRepository;
    private InMemoryItemRepo itemRepository;
    private InMemoryOrderRepo orderRepository;
    private OrderService orderService;
    private DummyNotifFacade notificationFacade;
    private DummyAuditFacade auditLogFacade;

    private AuctionScheduler scheduler;

    private Item item;

    @BeforeEach
    void setUp() {
        auctionRepository = new InMemoryAuctionRepo();
        itemRepository = new InMemoryItemRepo();
        orderRepository = new InMemoryOrderRepo();
        orderService = new OrderService(orderRepository);
        notificationFacade = new DummyNotifFacade();
        auditLogFacade = new DummyAuditFacade();

        scheduler = new AuctionScheduler(
                auctionRepository,
                itemRepository,
                orderService,
                notificationFacade,
                auditLogFacade
        );

        item = new Item();
        item.setId(50L);
        item.setSellerId(10L);
        item.setTitle("Sony PlayStation 5");
        itemRepository.save(item);
    }

    @Test
    @DisplayName("Hết giờ phiên đấu giá có người đặt và đạt giá sàn: chuyển sang ENDED_SOLD, tạo đơn hàng 72h và thông báo")
    void testProcessExpiredAuctions_SoldSuccessfully() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setItemId(item.getId());
        auction.setStartingPrice(5_000_000L);
        auction.setStepPrice(200_000L);
        auction.setCurrentPrice(8_000_000L);
        auction.setReservePrice(7_000_000L);
        auction.setReserveMet(true);
        auction.setHighestBidderId(20L); // buyerId
        auction.setBidCount(5);
        auction.setStatus(AuctionStatus.ACTIVE);
        auction.setStartTime(LocalDateTime.now().minusDays(3));
        auction.setEndTime(LocalDateTime.now().minusMinutes(1)); // expired!
        auctionRepository.save(auction);

        scheduler.processExpiredAuctions();

        Auction updated = auctionRepository.findById(auction.getId()).orElseThrow();
        assertEquals(AuctionStatus.ENDED_SOLD, updated.getStatus());

        // Verify order created
        Order order = orderRepository.findByAuctionId(auction.getId()).orElseThrow();
        assertEquals(20L, order.getBuyerId());
        assertEquals(10L, order.getSellerId());
        assertEquals(8_000_000L, order.getFinalPrice());
        assertEquals(OrderStatus.AWAITING_PAYMENT, order.getStatus());
        assertEquals(PurchaseType.AUCTION_WIN, order.getPurchaseType());
        assertTrue(order.getPaymentDeadline().isAfter(LocalDateTime.now().plusHours(71)));

        // Verify notifications
        assertEquals(2, notificationFacade.notifications.size());
        assertTrue(notificationFacade.notifications.stream().anyMatch(n -> n.contains("AUCTION_WON:20")));
        assertTrue(notificationFacade.notifications.stream().anyMatch(n -> n.contains("ITEM_SOLD:10")));
    }

    @Test
    @DisplayName("Hết giờ phiên đấu giá chưa đạt giá sàn: chuyển sang ENDED_UNSOLD và thông báo cho người bán")
    void testProcessExpiredAuctions_ReserveNotMet_Unsold() {
        Auction auction = new Auction();
        auction.setId(2L);
        auction.setItemId(item.getId());
        auction.setStartingPrice(5_000_000L);
        auction.setCurrentPrice(6_000_000L);
        auction.setReservePrice(10_000_000L);
        auction.setReserveMet(false); // Reserve not met!
        auction.setHighestBidderId(20L);
        auction.setBidCount(2);
        auction.setStatus(AuctionStatus.ACTIVE);
        auction.setStartTime(LocalDateTime.now().minusDays(3));
        auction.setEndTime(LocalDateTime.now().minusMinutes(1));
        auctionRepository.save(auction);

        scheduler.processExpiredAuctions();

        Auction updated = auctionRepository.findById(auction.getId()).orElseThrow();
        assertEquals(AuctionStatus.ENDED_UNSOLD, updated.getStatus());
        assertTrue(orderRepository.findByAuctionId(auction.getId()).isEmpty());

        // Verify notification sent to seller
        assertEquals(1, notificationFacade.notifications.size());
        assertTrue(notificationFacade.notifications.get(0).contains("AUCTION_UNSOLD:10"));
    }

    @Test
    @DisplayName("Hết giờ phiên đấu giá không có lượt đặt nào (bidCount == 0): chuyển sang ENDED_UNSOLD")
    void testProcessExpiredAuctions_ZeroBids_Unsold() {
        Auction auction = new Auction();
        auction.setId(3L);
        auction.setItemId(item.getId());
        auction.setStartingPrice(5_000_000L);
        auction.setCurrentPrice(5_000_000L);
        auction.setHighestBidderId(null);
        auction.setBidCount(0);
        auction.setStatus(AuctionStatus.ACTIVE);
        auction.setStartTime(LocalDateTime.now().minusDays(3));
        auction.setEndTime(LocalDateTime.now().minusMinutes(1));
        auctionRepository.save(auction);

        scheduler.processExpiredAuctions();

        Auction updated = auctionRepository.findById(auction.getId()).orElseThrow();
        assertEquals(AuctionStatus.ENDED_UNSOLD, updated.getStatus());
        assertTrue(orderRepository.findByAuctionId(auction.getId()).isEmpty());
    }

    @Test
    @DisplayName("Kích hoạt phiên đấu giá đã đến giờ bắt đầu: chuyển từ SCHEDULED sang ACTIVE")
    void testProcessScheduledAuctions_ActivatesWhenDue() {
        Auction auction = new Auction();
        auction.setId(4L);
        auction.setItemId(item.getId());
        auction.setStartingPrice(5_000_000L);
        auction.setStatus(AuctionStatus.SCHEDULED);
        auction.setStartTime(LocalDateTime.now().minusSeconds(10)); // due
        auction.setEndTime(LocalDateTime.now().plusDays(3));
        auctionRepository.save(auction);

        scheduler.processScheduledAuctions();

        Auction updated = auctionRepository.findById(auction.getId()).orElseThrow();
        assertEquals(AuctionStatus.ACTIVE, updated.getStatus());
    }

    // --- Doubles ---
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

    static class InMemoryAuctionRepo implements AuctionRepository {
        Map<Long, Auction> store = new ConcurrentHashMap<>();
        @Override public Optional<Auction> findByIdForUpdate(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public List<Auction> findByStatusOrderByCreatedAtDesc(AuctionStatus status) {
            return store.values().stream().filter(a -> a.getStatus() == status).toList();
        }
        @Override public List<Auction> findByItemIdOrderByCreatedAtDesc(Long itemId) {
            return store.values().stream().filter(a -> a.getItemId().equals(itemId)).toList();
        }
        @Override public List<Auction> findExpiredActiveAuctions(LocalDateTime now) {
            return store.values().stream().filter(a -> a.getStatus() == AuctionStatus.ACTIVE && a.getEndTime().isBefore(now)).toList();
        }
        @Override public List<Auction> findDueScheduledAuctions(LocalDateTime now) {
            return store.values().stream().filter(a -> a.getStatus() == AuctionStatus.SCHEDULED && a.getStartTime().isBefore(now)).toList();
        }
        @Override public <S extends Auction> S save(S entity) { store.put(entity.getId(), entity); return entity; }
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

    static class InMemoryOrderRepo implements OrderRepository {
        Map<Long, Order> store = new ConcurrentHashMap<>();
        AtomicLong seq = new AtomicLong(1);
        @Override public Optional<Order> findByAuctionId(Long auctionId) {
            return store.values().stream().filter(o -> o.getAuctionId().equals(auctionId)).findFirst();
        }
        @Override public List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId) { return Collections.emptyList(); }
        @Override public List<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId) { return Collections.emptyList(); }
        @Override public List<Order> findByStatusAndPaymentDeadlineBefore(OrderStatus status, LocalDateTime deadline) { return Collections.emptyList(); }
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
}
