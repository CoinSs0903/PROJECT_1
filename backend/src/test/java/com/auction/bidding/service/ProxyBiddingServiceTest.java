package com.auction.bidding.service;

import com.auction.auction.entity.Auction;
import com.auction.auction.entity.AuctionStatus;
import com.auction.auction.repository.AuctionRepository;
import com.auction.audit.facade.AuditLogFacade;
import com.auction.bidding.dto.BidResultDto;
import com.auction.bidding.entity.Bid;
import com.auction.bidding.entity.ProxyBid;
import com.auction.bidding.repository.BidRepository;
import com.auction.bidding.repository.ProxyBidRepository;
import com.auction.catalog.entity.Item;
import com.auction.catalog.repository.ItemRepository;
import com.auction.notification.facade.NotificationFacade;
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

class ProxyBiddingServiceTest {

    private InMemoryAuctionRepository auctionRepository;
    private InMemoryProxyBidRepository proxyBidRepository;
    private InMemoryBidRepository bidRepository;
    private InMemoryItemRepository itemRepository;
    private InMemoryUserRepository userRepository;
    private DummyNotificationFacade notificationFacade;
    private DummyAuditLogFacade auditLogFacade;

    private ProxyBiddingService proxyBiddingService;

    private Auction auction;
    private Item item;
    private User seller;
    private User bidder1;
    private User bidder2;

    @BeforeEach
    void setUp() {
        auctionRepository = new InMemoryAuctionRepository();
        proxyBidRepository = new InMemoryProxyBidRepository();
        bidRepository = new InMemoryBidRepository();
        itemRepository = new InMemoryItemRepository();
        userRepository = new InMemoryUserRepository();
        notificationFacade = new DummyNotificationFacade();
        auditLogFacade = new DummyAuditLogFacade();

        proxyBiddingService = new ProxyBiddingService(
                auctionRepository,
                proxyBidRepository,
                bidRepository,
                itemRepository,
                userRepository,
                notificationFacade,
                auditLogFacade
        );

        // Setup users
        seller = new User();
        seller.setId(1L);
        seller.setUsername("seller1");
        seller.setStatus(UserStatus.ACTIVE);
        userRepository.save(seller);

        bidder1 = new User();
        bidder1.setId(2L);
        bidder1.setUsername("bidder1");
        bidder1.setStatus(UserStatus.ACTIVE);
        userRepository.save(bidder1);

        bidder2 = new User();
        bidder2.setId(3L);
        bidder2.setUsername("bidder2");
        bidder2.setStatus(UserStatus.ACTIVE);
        userRepository.save(bidder2);

        // Setup item
        item = new Item();
        item.setId(10L);
        item.setSellerId(seller.getId());
        item.setTitle("iPhone 15 Pro");
        itemRepository.save(item);

        // Setup auction
        auction = new Auction();
        auction.setId(100L);
        auction.setItemId(item.getId());
        auction.setStartingPrice(1_000_000L);
        auction.setStepPrice(100_000L);
        auction.setCurrentPrice(1_000_000L);
        auction.setReservePrice(2_000_000L);
        auction.setBidCount(0);
        auction.setReserveMet(false);
        auction.setStatus(AuctionStatus.ACTIVE);
        auction.setStartTime(LocalDateTime.now().minusHours(1));
        auction.setEndTime(LocalDateTime.now().plusDays(3));
        auctionRepository.save(auction);
    }

    @Test
    @DisplayName("Đặt giá đầu tiên thành công và bắt đầu với giá khởi điểm")
    void testFirstBid_StartsAtStartingPrice() {
        BidResultDto result = proxyBiddingService.processBid(auction.getId(), bidder1.getId(), 1_500_000L);

        assertTrue(result.isSuccess());
        assertTrue(result.isLeading());
        assertEquals(1_000_000L, result.getCurrentPrice());
        assertEquals(1, result.getBidCount());
        assertFalse(result.isReserveMet());

        Auction updated = auctionRepository.findById(auction.getId()).orElseThrow();
        assertEquals(1_000_000L, updated.getCurrentPrice());
        assertEquals(bidder1.getId(), updated.getHighestBidderId());
        assertEquals(1, updated.getBidCount());
    }

    @Test
    @DisplayName("Đặt giá đầu tiên đạt hoặc vượt giá sàn thì giá hiện tại nhảy lên giá sàn")
    void testFirstBid_MeetsReservePrice_JumpsToReserve() {
        BidResultDto result = proxyBiddingService.processBid(auction.getId(), bidder1.getId(), 2_500_000L);

        assertTrue(result.isSuccess());
        assertTrue(result.isLeading());
        assertEquals(2_000_000L, result.getCurrentPrice(), "Phải nhảy lên giá sàn 2.000.000");
        assertTrue(result.isReserveMet());

        Auction updated = auctionRepository.findById(auction.getId()).orElseThrow();
        assertTrue(updated.isReserveMet());
        assertEquals(2_000_000L, updated.getCurrentPrice());
    }

    @Test
    @DisplayName("Người bán tự đặt giá sản phẩm của mình sẽ bị từ chối")
    void testSellerCannotBidOwnAuction() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                proxyBiddingService.processBid(auction.getId(), seller.getId(), 2_000_000L)
        );
        assertTrue(ex.getMessage().contains("Người bán không thể tự đặt giá"));
    }

    @Test
    @DisplayName("Đặt giá thấp hơn mức tối thiểu quy định sẽ bị từ chối")
    void testBidBelowMinimum_Rejected() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                proxyBiddingService.processBid(auction.getId(), bidder1.getId(), 500_000L)
        );
        assertTrue(ex.getMessage().contains("phải lớn hơn hoặc bằng mức giá tối thiểu"));
    }

    @Test
    @DisplayName("Người dẫn đầu cập nhật nâng giá tối đa thành công")
    void testLeaderIncreasesMaxAmount() {
        proxyBiddingService.processBid(auction.getId(), bidder1.getId(), 1_500_000L);

        BidResultDto result = proxyBiddingService.processBid(auction.getId(), bidder1.getId(), 1_800_000L);

        assertTrue(result.isSuccess());
        assertTrue(result.isLeading());
        ProxyBid proxyBid = proxyBidRepository.findByAuctionIdAndBidderId(auction.getId(), bidder1.getId()).orElseThrow();
        assertEquals(1_800_000L, proxyBid.getMaxAmount());
    }

    @Test
    @DisplayName("Người thứ hai đặt giá cao hơn người thứ nhất: Người thứ hai dẫn đầu với giá = max1 + step")
    void testSecondBidderOutbidsFirstBidder() {
        // Bidder 1 bids max 1.5M (start 1.0M)
        proxyBiddingService.processBid(auction.getId(), bidder1.getId(), 1_500_000L);

        // Bidder 2 bids max 2.2M (beats 1.5M) -> new price = 1.5M + 100k = 1.6M
        // But reserve price is 2.0M, and 2.2M >= 2.0M -> price jumps to min(2.2M, 2.0M) = 2.0M!
        BidResultDto result = proxyBiddingService.processBid(auction.getId(), bidder2.getId(), 2_200_000L);

        assertTrue(result.isSuccess());
        assertTrue(result.isLeading());
        assertEquals(2_000_000L, result.getCurrentPrice());
        assertTrue(result.isReserveMet());

        Auction updated = auctionRepository.findById(auction.getId()).orElseThrow();
        assertEquals(bidder2.getId(), updated.getHighestBidderId());

        // Bidder 1 should receive OUTBID notification
        assertEquals(1, notificationFacade.notifications.size());
        DummyNotificationFacade.NotifRecord notif = notificationFacade.notifications.get(0);
        assertEquals(bidder1.getId(), notif.userId);
        assertEquals("OUTBID", notif.type);
    }

    @Test
    @DisplayName("Người thứ hai đặt giá thấp hơn giá tối đa của người thứ nhất: Người thứ nhất tiếp tục dẫn đầu và người thứ hai bị vượt ngay")
    void testSecondBidderFailsToBeatFirstBidder() {
        // Bidder 1 bids max 1.8M
        proxyBiddingService.processBid(auction.getId(), bidder1.getId(), 1_800_000L);

        // Bidder 2 bids max 1.3M (< 1.8M)
        // Price jumps to min(1.8M, 1.3M + 100k) = 1.4M
        BidResultDto result = proxyBiddingService.processBid(auction.getId(), bidder2.getId(), 1_300_000L);

        assertTrue(result.isSuccess());
        assertFalse(result.isLeading(), "Bidder 2 không thể dẫn đầu");
        assertEquals(1_400_000L, result.getCurrentPrice());

        Auction updated = auctionRepository.findById(auction.getId()).orElseThrow();
        assertEquals(bidder1.getId(), updated.getHighestBidderId(), "Bidder 1 vẫn dẫn đầu");
        assertEquals(1_400_000L, updated.getCurrentPrice());

        // Bidder 2 should receive OUTBID immediately notification
        assertEquals(1, notificationFacade.notifications.size());
        DummyNotificationFacade.NotifRecord notif = notificationFacade.notifications.get(0);
        assertEquals(bidder2.getId(), notif.userId);
        assertEquals("OUTBID", notif.type);
    }

    // --- In-memory test doubles ---
    static class DummyNotificationFacade implements NotificationFacade {
        static class NotifRecord {
            Long userId;
            String type;
            String title;
            String content;
            String url;
            NotifRecord(Long u, String t, String ti, String c, String url) {
                this.userId = u; this.type = t; this.title = ti; this.content = c; this.url = url;
            }
        }
        List<NotifRecord> notifications = new ArrayList<>();
        @Override
        public void sendNotification(Long userId, String type, String title, String content, String actionUrl) {
            notifications.add(new NotifRecord(userId, type, title, content, actionUrl));
        }
        @Override public int getUnreadCount(Long userId) { return 0; }
        @Override public void markAsRead(Long notificationId, Long userId) {}
    }

    static class DummyAuditLogFacade implements AuditLogFacade {
        @Override
        public void log(String action, Long userId, String entityName, Long entityId, String details) {}
    }

    static class InMemoryAuctionRepository implements AuctionRepository {
        Map<Long, Auction> store = new ConcurrentHashMap<>();
        AtomicLong seq = new AtomicLong(100);
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

    static class InMemoryProxyBidRepository implements ProxyBidRepository {
        Map<Long, ProxyBid> store = new ConcurrentHashMap<>();
        AtomicLong seq = new AtomicLong(1);
        @Override public Optional<ProxyBid> findByAuctionIdAndBidderId(Long auctionId, Long bidderId) {
            return store.values().stream().filter(p -> p.getAuctionId().equals(auctionId) && p.getBidderId().equals(bidderId)).findFirst();
        }
        @Override public Optional<ProxyBid> findTopByAuctionIdOrderByMaxAmountDescCreatedAtAsc(Long auctionId) {
            return store.values().stream().filter(p -> p.getAuctionId().equals(auctionId))
                    .sorted((a, b) -> {
                        int cmp = Long.compare(b.getMaxAmount(), a.getMaxAmount());
                        return cmp != 0 ? cmp : Long.compare(a.getId(), b.getId());
                    }).findFirst();
        }
        @Override public List<ProxyBid> findByAuctionIdOrderByMaxAmountDesc(Long auctionId) {
            return store.values().stream().filter(p -> p.getAuctionId().equals(auctionId))
                    .sorted((a, b) -> Long.compare(b.getMaxAmount(), a.getMaxAmount())).toList();
        }
        @Override public <S extends ProxyBid> S save(S entity) {
            if (entity.getId() == null) entity.setId(seq.incrementAndGet());
            store.put(entity.getId(), entity);
            return entity;
        }
        @Override public Optional<ProxyBid> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(ProxyBid entity) { store.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends ProxyBid> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<ProxyBid> findAll() { return new ArrayList<>(store.values()); }
        @Override public List<ProxyBid> findAllById(Iterable<Long> ids) { return Collections.emptyList(); }
        @Override public <S extends ProxyBid> List<S> saveAll(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void flush() {}
        @Override public <S extends ProxyBid> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends ProxyBid> List<S> saveAllAndFlush(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void deleteAllInBatch(Iterable<ProxyBid> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public ProxyBid getOne(Long id) { return store.get(id); }
        @Override public ProxyBid getById(Long id) { return store.get(id); }
        @Override public ProxyBid getReferenceById(Long id) { return store.get(id); }
        @Override public <S extends ProxyBid> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends ProxyBid> List<S> findAll(Example<S> example) { return Collections.emptyList(); }
        @Override public <S extends ProxyBid> List<S> findAll(Example<S> example, Sort sort) { return Collections.emptyList(); }
        @Override public <S extends ProxyBid> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends ProxyBid> long count(Example<S> example) { return 0; }
        @Override public <S extends ProxyBid> boolean exists(Example<S> example) { return false; }
        @Override public <S extends ProxyBid, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<ProxyBid> findAll(Sort sort) { return Collections.emptyList(); }
        @Override public Page<ProxyBid> findAll(Pageable pageable) { return null; }
    }

    static class InMemoryBidRepository implements BidRepository {
        Map<Long, Bid> store = new ConcurrentHashMap<>();
        AtomicLong seq = new AtomicLong(1);
        @Override public List<Bid> findByAuctionIdOrderByBidTimeDesc(Long auctionId) {
            return store.values().stream().filter(b -> b.getAuctionId().equals(auctionId)).toList();
        }
        @Override public int countByAuctionId(Long auctionId) {
            return (int) store.values().stream().filter(b -> b.getAuctionId().equals(auctionId)).count();
        }
        @Override public <S extends Bid> S save(S entity) {
            if (entity.getId() == null) entity.setId(seq.incrementAndGet());
            store.put(entity.getId(), entity);
            return entity;
        }
        @Override public Optional<Bid> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(Bid entity) { store.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends Bid> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<Bid> findAll() { return new ArrayList<>(store.values()); }
        @Override public List<Bid> findAllById(Iterable<Long> ids) { return Collections.emptyList(); }
        @Override public <S extends Bid> List<S> saveAll(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void flush() {}
        @Override public <S extends Bid> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Bid> List<S> saveAllAndFlush(Iterable<S> entities) { return Collections.emptyList(); }
        @Override public void deleteAllInBatch(Iterable<Bid> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public Bid getOne(Long id) { return store.get(id); }
        @Override public Bid getById(Long id) { return store.get(id); }
        @Override public Bid getReferenceById(Long id) { return store.get(id); }
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

    static class InMemoryItemRepository implements ItemRepository {
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

    static class InMemoryUserRepository implements UserRepository {
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
