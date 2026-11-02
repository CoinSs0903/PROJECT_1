package com.auction.auction.service;

import com.auction.auction.dto.AuctionDetailDto;
import com.auction.auction.dto.AuctionResponseDto;
import com.auction.auction.dto.CreateAuctionRequest;
import com.auction.auction.entity.Auction;
import com.auction.auction.entity.AuctionStatus;
import com.auction.auction.repository.AuctionRepository;
import com.auction.audit.facade.AuditLogFacade;
import com.auction.bidding.entity.Bid;
import com.auction.bidding.repository.BidRepository;
import com.auction.catalog.entity.Category;
import com.auction.catalog.entity.Item;
import com.auction.catalog.entity.ItemCondition;
import com.auction.catalog.entity.ItemImage;
import com.auction.catalog.repository.CategoryRepository;
import com.auction.catalog.repository.ItemImageRepository;
import com.auction.catalog.repository.ItemRepository;
import com.auction.notification.facade.NotificationFacade;
import com.auction.order.entity.Order;
import com.auction.order.entity.OrderStatus;
import com.auction.order.entity.PurchaseType;
import com.auction.order.repository.OrderRepository;
import com.auction.order.service.OrderService;
import com.auction.user.entity.User;
import com.auction.user.entity.UserStatus;
import com.auction.user.repository.UserRepository;
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

class AuctionServiceTest {

    private InMemoryAuctionRepo auctionRepository;
    private InMemoryItemRepo itemRepository;
    private InMemoryItemImageRepo itemImageRepository;
    private InMemoryCategoryRepo categoryRepository;
    private InMemoryUserRepo userRepository;
    private InMemoryBidRepo bidRepository;
    private InMemoryOrderRepo orderRepository;
    private OrderService orderService;
    private DummyNotifFacade notificationFacade;
    private DummyAuditFacade auditLogFacade;

    private AuctionService auctionService;

    private User seller;
    private User buyer;
    private Category category;
    private Item item;

    @BeforeEach
    void setUp() {
        auctionRepository = new InMemoryAuctionRepo();
        itemRepository = new InMemoryItemRepo();
        itemImageRepository = new InMemoryItemImageRepo();
        categoryRepository = new InMemoryCategoryRepo();
        userRepository = new InMemoryUserRepo();
        bidRepository = new InMemoryBidRepo();
        orderRepository = new InMemoryOrderRepo();

        orderService = new OrderService(orderRepository);
        notificationFacade = new DummyNotifFacade();
        auditLogFacade = new DummyAuditFacade();

        auctionService = new AuctionService(
                auctionRepository,
                itemRepository,
                itemImageRepository,
                categoryRepository,
                userRepository,
                bidRepository,
                orderService,
                notificationFacade,
                auditLogFacade
        );

        seller = new User();
        seller.setId(1L);
        seller.setUsername("seller_pro");
        seller.setStatus(UserStatus.ACTIVE);
        userRepository.save(seller);

        buyer = new User();
        buyer.setId(2L);
        buyer.setUsername("buyer_cool");
        buyer.setStatus(UserStatus.ACTIVE);
        userRepository.save(buyer);

        category = new Category();
        category.setId(10L);
        category.setName("Laptop & Máy Tính");
        categoryRepository.save(category);

        item = new Item();
        item.setId(100L);
        item.setSellerId(seller.getId());
        item.setCategoryId(category.getId());
        item.setTitle("MacBook Pro M3 Max");
        item.setDescription("Hàng nguyên seal chính hãng");
        item.setItemCondition(ItemCondition.NEW);
        item.setMainImageUrl("https://example.com/macbook.jpg");
        itemRepository.save(item);
    }

    @Test
    @DisplayName("Tạo phiên đấu giá thành công với các thông số hợp lệ")
    void testCreateAuction_Success() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setItemId(item.getId());
        req.setStartingPrice(10_000_000L);
        req.setStepPrice(500_000L);
        req.setReservePrice(15_000_000L);
        req.setBuyItNowPrice(20_000_000L);
        req.setDurationDays(5);

        Auction created = auctionService.createAuction(seller.getId(), req);

        assertNotNull(created.getId());
        assertEquals(item.getId(), created.getItemId());
        assertEquals(10_000_000L, created.getStartingPrice());
        assertEquals(500_000L, created.getStepPrice());
        assertEquals(15_000_000L, created.getReservePrice());
        assertEquals(20_000_000L, created.getBuyItNowPrice());
        assertEquals(AuctionStatus.ACTIVE, created.getStatus());
        assertEquals(0, created.getBidCount());
        assertFalse(created.isReserveMet());
    }

    @Test
    @DisplayName("Tạo phiên đấu giá thất bại nếu người tạo không phải chủ món hàng")
    void testCreateAuction_NotOwner_Throws() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setItemId(item.getId());
        req.setStartingPrice(10_000_000L);
        req.setStepPrice(500_000L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                auctionService.createAuction(buyer.getId(), req)
        );
        assertTrue(ex.getMessage().contains("không có quyền"));
    }

    @Test
    @DisplayName("Tạo phiên đấu giá thất bại nếu giá sàn nhỏ hơn giá khởi điểm")
    void testCreateAuction_ReserveLessThanStarting_Throws() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setItemId(item.getId());
        req.setStartingPrice(10_000_000L);
        req.setStepPrice(500_000L);
        req.setReservePrice(8_000_000L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                auctionService.createAuction(seller.getId(), req)
        );
        assertTrue(ex.getMessage().contains("Giá sàn (Reserve Price) phải lớn hơn hoặc bằng"));
    }

    @Test
    @DisplayName("Tạo phiên đấu giá thất bại nếu giá mua ngay nhỏ hơn hoặc bằng giá sàn")
    void testCreateAuction_BuyItNowNotHigherThanReserve_Throws() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setItemId(item.getId());
        req.setStartingPrice(10_000_000L);
        req.setStepPrice(500_000L);
        req.setReservePrice(15_000_000L);
        req.setBuyItNowPrice(15_000_000L); // Bằng giá sàn

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                auctionService.createAuction(seller.getId(), req)
        );
        assertTrue(ex.getMessage().contains("Giá mua ngay phải lớn hơn"));
    }

    @Test
    @DisplayName("Mua ngay Buy It Now thành công: kết thúc phiên, tạo đơn hàng và thông báo")
    void testBuyItNow_Success() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setItemId(item.getId());
        req.setStartingPrice(10_000_000L);
        req.setStepPrice(500_000L);
        req.setBuyItNowPrice(20_000_000L);
        Auction created = auctionService.createAuction(seller.getId(), req);

        Order order = auctionService.executeBuyItNow(created.getId(), buyer.getId());

        assertNotNull(order);
        assertEquals(created.getId(), order.getAuctionId());
        assertEquals(buyer.getId(), order.getBuyerId());
        assertEquals(seller.getId(), order.getSellerId());
        assertEquals(PurchaseType.BUY_IT_NOW, order.getPurchaseType());
        assertEquals(20_000_000L, order.getFinalPrice());
        assertEquals(OrderStatus.AWAITING_PAYMENT, order.getStatus());

        Auction updated = auctionRepository.findById(created.getId()).orElseThrow();
        assertEquals(AuctionStatus.ENDED_SOLD, updated.getStatus());
        assertEquals(20_000_000L, updated.getCurrentPrice());

        // Notifications sent to both buyer and seller
        assertEquals(2, notificationFacade.notifications.size());
    }

    @Test
    @DisplayName("Người bán tự mua ngay sản phẩm của mình sẽ bị từ chối")
    void testBuyItNow_SellerCannotBuyOwnItem() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setItemId(item.getId());
        req.setStartingPrice(10_000_000L);
        req.setStepPrice(500_000L);
        req.setBuyItNowPrice(20_000_000L);
        Auction created = auctionService.createAuction(seller.getId(), req);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                auctionService.executeBuyItNow(created.getId(), seller.getId())
        );
        assertTrue(ex.getMessage().contains("không thể mua ngay sản phẩm do chính mình đăng bán"));
    }

    @Test
    @DisplayName("Người bán huỷ phiên đấu giá thành công khi chưa có lượt đặt giá")
    void testCancelAuction_SuccessWhenZeroBids() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setItemId(item.getId());
        req.setStartingPrice(10_000_000L);
        req.setStepPrice(500_000L);
        Auction created = auctionService.createAuction(seller.getId(), req);

        auctionService.cancelAuction(created.getId(), seller.getId());

        Auction updated = auctionRepository.findById(created.getId()).orElseThrow();
        assertEquals(AuctionStatus.CANCELED, updated.getStatus());
    }

    @Test
    @DisplayName("Không thể huỷ phiên đấu giá khi đã có người đặt giá")
    void testCancelAuction_FailsWhenBidsExist() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setItemId(item.getId());
        req.setStartingPrice(10_000_000L);
        req.setStepPrice(500_000L);
        Auction created = auctionService.createAuction(seller.getId(), req);

        created.setBidCount(1);
        auctionRepository.save(created);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                auctionService.cancelAuction(created.getId(), seller.getId())
        );
        assertTrue(ex.getMessage().contains("Không thể huỷ phiên đấu giá khi đã có người đặt giá"));
    }

    @Test
    @DisplayName("Xem chi tiết phiên đấu giá không tiết lộ giá sàn bí mật và ẩn danh người dẫn đầu")
    void testGetAuctionDetail_HidesReservePriceAndMasksBidder() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setItemId(item.getId());
        req.setStartingPrice(10_000_000L);
        req.setStepPrice(500_000L);
        req.setReservePrice(18_000_000L);
        Auction created = auctionService.createAuction(seller.getId(), req);
        created.setHighestBidderId(buyer.getId());
        created.setBidCount(1);
        auctionRepository.save(created);

        AuctionDetailDto detail = auctionService.getAuctionDetail(created.getId(), seller.getId());

        assertNotNull(detail);
        assertEquals("MacBook Pro M3 Max", detail.getItemTitle());
        assertEquals("b***l", detail.getHighestBidderMasked(), "Tên người dẫn đầu phải được ẩn danh BR-24");
        assertFalse(detail.isReserveMet());
        assertTrue(detail.isUserIsSeller());
    }

    // --- In-memory repos ---
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
        AtomicLong seq = new AtomicLong(1);
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

    static class InMemoryItemImageRepo implements ItemImageRepository {
        @Override public List<ItemImage> findByItemIdOrderByDisplayOrderAsc(Long itemId) { return Collections.emptyList(); }
        @Override public void deleteByItemId(Long itemId) {}
        @Override public <S extends ItemImage> S save(S entity) { return entity; }
        @Override public <S extends ItemImage> List<S> saveAll(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public Optional<ItemImage> findById(Long id) { return Optional.empty(); }
        @Override public boolean existsById(Long id) { return false; }
        @Override public List<ItemImage> findAll() { return Collections.emptyList(); }
        @Override public List<ItemImage> findAllById(Iterable<Long> ids) { return Collections.emptyList(); }
        @Override public long count() { return 0; }
        @Override public void deleteById(Long id) {}
        @Override public void delete(ItemImage entity) {}
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends ItemImage> entities) {}
        @Override public void deleteAll() {}
        @Override public void flush() {}
        @Override public <S extends ItemImage> S saveAndFlush(S entity) { return entity; }
        @Override public <S extends ItemImage> List<S> saveAllAndFlush(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void deleteAllInBatch(Iterable<ItemImage> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public ItemImage getOne(Long id) { return null; }
        @Override public ItemImage getById(Long id) { return null; }
        @Override public ItemImage getReferenceById(Long id) { return null; }
        @Override public <S extends ItemImage> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends ItemImage> List<S> findAll(Example<S> example) { return Collections.emptyList(); }
        @Override public <S extends ItemImage> List<S> findAll(Example<S> example, Sort sort) { return Collections.emptyList(); }
        @Override public <S extends ItemImage> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends ItemImage> long count(Example<S> example) { return 0; }
        @Override public <S extends ItemImage> boolean exists(Example<S> example) { return false; }
        @Override public <S extends ItemImage, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<ItemImage> findAll(Sort sort) { return Collections.emptyList(); }
        @Override public Page<ItemImage> findAll(Pageable pageable) { return null; }
    }

    static class InMemoryCategoryRepo implements CategoryRepository {
        Map<Long, Category> store = new ConcurrentHashMap<>();
        @Override public Optional<Category> findBySlug(String slug) { return Optional.empty(); }
        @Override public List<Category> findByParentIdIsNullOrderByDisplayOrderAsc() { return Collections.emptyList(); }
        @Override public List<Category> findByParentIdOrderByDisplayOrderAsc(Long parentId) { return Collections.emptyList(); }
        @Override public <S extends Category> S save(S entity) { store.put(entity.getId(), entity); return entity; }
        @Override public Optional<Category> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(Category entity) { store.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends Category> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<Category> findAll() { return new ArrayList<>(store.values()); }
        @Override public List<Category> findAllById(Iterable<Long> ids) { return Collections.emptyList(); }
        @Override public <S extends Category> List<S> saveAll(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void flush() {}
        @Override public <S extends Category> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Category> List<S> saveAllAndFlush(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void deleteAllInBatch(Iterable<Category> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public Category getOne(Long id) { return store.get(id); }
        @Override public Category getById(Long id) { return store.get(id); }
        @Override public Category getReferenceById(Long id) { return store.get(id); }
        @Override public <S extends Category> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends Category> List<S> findAll(Example<S> example) { return Collections.emptyList(); }
        @Override public <S extends Category> List<S> findAll(Example<S> example, Sort sort) { return Collections.emptyList(); }
        @Override public <S extends Category> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends Category> long count(Example<S> example) { return 0; }
        @Override public <S extends Category> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Category, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<Category> findAll(Sort sort) { return Collections.emptyList(); }
        @Override public Page<Category> findAll(Pageable pageable) { return null; }
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

    static class InMemoryBidRepo implements BidRepository {
        @Override public List<Bid> findByAuctionIdOrderByBidTimeDesc(Long auctionId) { return Collections.emptyList(); }
        @Override public int countByAuctionId(Long auctionId) { return 0; }
        @Override public <S extends Bid> S save(S entity) { return entity; }
        @Override public <S extends Bid> List<S> saveAll(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public Optional<Bid> findById(Long id) { return Optional.empty(); }
        @Override public boolean existsById(Long id) { return false; }
        @Override public List<Bid> findAll() { return Collections.emptyList(); }
        @Override public List<Bid> findAllById(Iterable<Long> ids) { return Collections.emptyList(); }
        @Override public long count() { return 0; }
        @Override public void deleteById(Long id) {}
        @Override public void delete(Bid entity) {}
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends Bid> entities) {}
        @Override public void deleteAll() {}
        @Override public void flush() {}
        @Override public <S extends Bid> S saveAndFlush(S entity) { return entity; }
        @Override public <S extends Bid> List<S> saveAllAndFlush(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void deleteAllInBatch(Iterable<Bid> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public Bid getOne(Long id) { return null; }
        @Override public Bid getById(Long id) { return null; }
        @Override public Bid getReferenceById(Long id) { return null; }
        @Override public <S extends Bid> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends Bid> List<S> findAll(Example<S> example) { return Collections.emptyList(); }
        @Override public <S extends Bid> List<S> findAll(Example<S> example, Sort sort) { return Collections.emptyList(); }
        @Override public <S extends Bid> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends Bid> long count(Example<S> example) { return 0; }
        @Override public <S extends Bid> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Bid, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<Bid> findAll(Sort sort) { return Collections.emptyList(); }
        @Override public Page<Bid> findAll(Pageable pageable) { return null; }
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
