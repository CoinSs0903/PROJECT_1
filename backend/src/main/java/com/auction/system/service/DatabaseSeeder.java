package com.auction.system.service;

import com.auction.auction.entity.Auction;
import com.auction.auction.entity.AuctionStatus;
import com.auction.auction.repository.AuctionRepository;
import com.auction.bidding.service.ProxyBiddingService;
import com.auction.catalog.entity.Category;
import com.auction.catalog.entity.Item;
import com.auction.catalog.entity.ItemCondition;
import com.auction.catalog.repository.CategoryRepository;
import com.auction.catalog.repository.ItemRepository;
import com.auction.order.entity.Order;
import com.auction.order.entity.OrderStatus;
import com.auction.order.entity.PurchaseType;
import com.auction.order.repository.OrderRepository;
import com.auction.user.entity.User;
import com.auction.user.entity.UserRole;
import com.auction.user.entity.UserStatus;
import com.auction.user.repository.UserRepository;
import com.auction.wallet.entity.Wallet;
import com.auction.wallet.entity.WalletType;
import com.auction.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DatabaseSeeder {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final AuctionRepository auctionRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProxyBiddingService proxyBiddingService;

    public DatabaseSeeder(
            UserRepository userRepository,
            WalletRepository walletRepository,
            CategoryRepository categoryRepository,
            ItemRepository itemRepository,
            AuctionRepository auctionRepository,
            OrderRepository orderRepository,
            PasswordEncoder passwordEncoder,
            ProxyBiddingService proxyBiddingService) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.auctionRepository = auctionRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
        this.proxyBiddingService = proxyBiddingService;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedDemoDataIfEmpty() {
        // Ensure SYSTEM_REVENUE wallet exists
        if (walletRepository.findByWalletType(WalletType.SYSTEM_REVENUE).isEmpty()) {
            Wallet sysWallet = new Wallet();
            sysWallet.setUserId(null);
            sysWallet.setWalletType(WalletType.SYSTEM_REVENUE);
            sysWallet.setAvailableBalance(0L);
            walletRepository.save(sysWallet);
        }

        // 1. Seed Admin
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@auction.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setFullName("Quản Trị Viên Hệ Thống");
            admin.setPhoneNumber("0900000001");
            admin.setRole(UserRole.ROLE_ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            userRepository.save(admin);
            createWallet(admin.getId(), 100_000_000L);
            log.info("Đã khởi tạo tài khoản admin: admin / admin123");
        }

        // 2. Seed Sellers & Buyers if empty
        if (userRepository.findByUsername("seller1").isEmpty()) {
            User s1 = createUser("seller1", "seller1@gmail.com", "Nguyễn Văn Bán", "0911111111", UserRole.ROLE_USER, 20_000_000L);
            User s2 = createUser("seller2", "seller2@gmail.com", "Trần Thị Bán", "0922222222", UserRole.ROLE_USER, 15_000_000L);
            User b1 = createUser("buyer1", "buyer1@gmail.com", "Lê Văn Mua", "0933333333", UserRole.ROLE_USER, 50_000_000L);
            User b2 = createUser("buyer2", "buyer2@gmail.com", "Phạm Thị Mua", "0944444444", UserRole.ROLE_USER, 80_000_000L);
            User b3 = createUser("buyer3", "buyer3@gmail.com", "Hoàng Văn Mua", "0955555555", UserRole.ROLE_USER, 10_000_000L);

            // 3. Seed Items & Auctions
            List<Category> categories = categoryRepository.findAll();
            Long catElectronics = categories.isEmpty() ? 1L : categories.get(0).getId();
            Long catFashion = categories.size() > 1 ? categories.get(1).getId() : catElectronics;

            // Item 1: iPhone 15 Pro Max
            Item item1 = createItem(s1.getId(), catElectronics, "iPhone 15 Pro Max 256GB Titan Tự Nhiên",
                    "Máy nguyên hộp, mới 100%, bảo hành chính hãng Apple VN 12 tháng.",
                    ItemCondition.NEW, "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=60");

            Auction a1 = createAuction(item1.getId(), 15_000_000L, 500_000L, 22_000_000L, 27_000_000L, 3);
            // Place bids on Auction 1
            proxyBiddingService.processBid(a1.getId(), b1.getId(), 20_000_000L);
            proxyBiddingService.processBid(a1.getId(), b2.getId(), 24_000_000L);

            // Item 2: MacBook Pro M3
            Item item2 = createItem(s1.getId(), catElectronics, "MacBook Pro 14 inch M3 Pro 18GB/512GB Space Black",
                    "Hàng like new 99%, pin 100%, sạc cáp zin đi kèm đầy đủ.",
                    ItemCondition.LIKE_NEW, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&auto=format&fit=crop&q=60");
            Auction a2 = createAuction(item2.getId(), 25_000_000L, 1_000_000L, 35_000_000L, 42_000_000L, 5);
            proxyBiddingService.processBid(a2.getId(), b1.getId(), 30_000_000L);

            // Item 3: Đồng hồ cơ Thụy Sĩ Tissot
            Item item3 = createItem(s2.getId(), catFashion, "Đồng Hồ Cơ Nam Tissot Powermatic 80 Thụy Sĩ",
                    "Mặt kính Sapphire nguyên khối, trữ cót 80 giờ, độ mới 98%.",
                    ItemCondition.LIKE_NEW, "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=600&auto=format&fit=crop&q=60");
            createAuction(item3.getId(), 5_000_000L, 200_000L, 8_000_000L, 11_000_000L, 4);

            // Item 4: Máy ảnh Sony Alpha A7 IV
            Item item4 = createItem(s2.getId(), catElectronics, "Máy Ảnh Sony Alpha A7 Mark IV Body",
                    "Chụp 2k shot, sensor sạch đẹp không bụi mốc, hàng chính hãng Sony VN.",
                    ItemCondition.LIKE_NEW, "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&auto=format&fit=crop&q=60");
            Auction a4 = createAuction(item4.getId(), 30_000_000L, 500_000L, 40_000_000L, 48_000_000L, 7);

            // Item 5: Túi xách hàng hiệu
            Item item5 = createItem(s2.getId(), catFashion, "Túi Xách Da Cao Cấp Phong Cách Cổ Điển",
                    "Chất liệu da bò thật cao cấp, thiết kế sang trọng thanh lịch.",
                    ItemCondition.NEW, "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&auto=format&fit=crop&q=60");
            Auction a5 = createAuction(item5.getId(), 2_000_000L, 100_000L, 3_500_000L, 5_000_000L, 3);
            proxyBiddingService.processBid(a5.getId(), b3.getId(), 2_500_000L);

            // Create 1 completed demo Order for workflow showcase
            Item itemSold = createItem(s1.getId(), catElectronics, "Tai nghe AirPods Pro 2 USB-C",
                    "Tai nghe chính hãng nguyên seal chưa active.",
                    ItemCondition.NEW, "https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=600&auto=format&fit=crop&q=60");
            Auction aSold = createAuction(itemSold.getId(), 3_000_000L, 100_000L, 4_000_000L, 5_000_000L, 1);
            aSold.setStatus(AuctionStatus.ENDED_SOLD);
            aSold.setCurrentPrice(4_500_000L);
            aSold.setHighestBidderId(b1.getId());
            aSold.setBidCount(3);
            aSold.setReserveMet(true);
            auctionRepository.save(aSold);

            Order demoOrder = new Order();
            demoOrder.setAuctionId(aSold.getId());
            demoOrder.setBuyerId(b1.getId());
            demoOrder.setSellerId(s1.getId());
            demoOrder.setPurchaseType(PurchaseType.AUCTION_WIN);
            demoOrder.setFinalPrice(4_500_000L);
            demoOrder.setShippingFee(0L);
            demoOrder.setTotalAmount(4_500_000L);
            demoOrder.setStatus(OrderStatus.AWAITING_PAYMENT);
            demoOrder.setPaymentDeadline(LocalDateTime.now().plusHours(48));
            orderRepository.save(demoOrder);

            log.info("Đã nạp dữ liệu demo phong phú: 6 món hàng, 5 phiên đấu giá, người dùng mẫu.");
        }
    }

    private User createUser(String username, String email, String fullName, String phone, UserRole role, long balance) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode("123456"));
        u.setFullName(fullName);
        u.setPhoneNumber(phone);
        u.setRole(role);
        u.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(u);
        createWallet(saved.getId(), balance);
        return saved;
    }

    private void createWallet(Long userId, long balance) {
        Wallet w = new Wallet();
        w.setUserId(userId);
        w.setWalletType(WalletType.USER);
        w.setAvailableBalance(balance);
        walletRepository.save(w);
    }

    private Item createItem(Long sellerId, Long categoryId, String title, String desc, ItemCondition cond, String img) {
        Item item = new Item();
        item.setSellerId(sellerId);
        item.setCategoryId(categoryId);
        item.setTitle(title);
        item.setDescription(desc);
        item.setItemCondition(cond);
        item.setMainImageUrl(img);
        return itemRepository.save(item);
    }

    private Auction createAuction(Long itemId, long startPrice, long stepPrice, Long reservePrice, Long buyItNowPrice, int days) {
        Auction a = new Auction();
        a.setItemId(itemId);
        a.setStartingPrice(startPrice);
        a.setStepPrice(stepPrice);
        a.setReservePrice(reservePrice);
        a.setBuyItNowPrice(buyItNowPrice);
        a.setCurrentPrice(startPrice);
        a.setBidCount(0);
        a.setReserveMet(false);
        a.setStartTime(LocalDateTime.now());
        a.setEndTime(LocalDateTime.now().plusDays(days));
        a.setStatus(AuctionStatus.ACTIVE);
        return auctionRepository.save(a);
    }
}

