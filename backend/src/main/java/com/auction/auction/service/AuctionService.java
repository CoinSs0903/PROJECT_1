package com.auction.auction.service;

import com.auction.auction.dto.AuctionDetailDto;
import com.auction.auction.dto.AuctionResponseDto;
import com.auction.auction.dto.CreateAuctionRequest;
import com.auction.auction.entity.Auction;
import com.auction.auction.entity.AuctionStatus;
import com.auction.auction.repository.AuctionRepository;
import com.auction.audit.facade.AuditLogFacade;
import com.auction.bidding.dto.BidHistoryDto;
import com.auction.bidding.entity.Bid;
import com.auction.bidding.repository.BidRepository;
import com.auction.catalog.entity.Category;
import com.auction.catalog.entity.Item;
import com.auction.catalog.entity.ItemImage;
import com.auction.catalog.repository.CategoryRepository;
import com.auction.catalog.repository.ItemImageRepository;
import com.auction.catalog.repository.ItemRepository;
import com.auction.common.util.StringUtil;
import com.auction.notification.facade.NotificationFacade;
import com.auction.order.entity.Order;
import com.auction.order.entity.PurchaseType;
import com.auction.order.service.OrderService;
import com.auction.user.entity.User;
import com.auction.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final OrderService orderService;
    private final NotificationFacade notificationFacade;
    private final AuditLogFacade auditLogFacade;

    public AuctionService(
            AuctionRepository auctionRepository,
            ItemRepository itemRepository,
            ItemImageRepository itemImageRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            BidRepository bidRepository,
            OrderService orderService,
            NotificationFacade notificationFacade,
            AuditLogFacade auditLogFacade) {
        this.auctionRepository = auctionRepository;
        this.itemRepository = itemRepository;
        this.itemImageRepository = itemImageRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.orderService = orderService;
        this.notificationFacade = notificationFacade;
        this.auditLogFacade = auditLogFacade;
    }

    @Transactional
    public Auction createAuction(Long sellerId, CreateAuctionRequest req) {
        Item item = itemRepository.findById(req.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại: " + req.getItemId()));

        if (!item.getSellerId().equals(sellerId)) {
            throw new IllegalArgumentException("Bạn không có quyền đấu giá sản phẩm này.");
        }

        List<Auction> existing = auctionRepository.findByItemIdOrderByCreatedAtDesc(item.getId());
        boolean hasActive = existing.stream().anyMatch(a -> a.getStatus() == AuctionStatus.ACTIVE || a.getStatus() == AuctionStatus.SCHEDULED);
        if (hasActive) {
            throw new IllegalStateException("Sản phẩm này đang có một phiên đấu giá đang diễn ra hoặc đã lên lịch.");
        }

        if (req.getStartingPrice() < 0) {
            throw new IllegalArgumentException("Giá khởi điểm không được âm.");
        }
        if (req.getStepPrice() <= 0) {
            throw new IllegalArgumentException("Bước giá phải lớn hơn 0.");
        }
        if (req.getReservePrice() != null && req.getReservePrice() < req.getStartingPrice()) {
            throw new IllegalArgumentException("Giá sàn (Reserve Price) phải lớn hơn hoặc bằng giá khởi điểm.");
        }
        if (req.getBuyItNowPrice() != null) {
            long threshold = (req.getReservePrice() != null) ? req.getReservePrice() : req.getStartingPrice();
            if (req.getBuyItNowPrice() <= threshold) {
                throw new IllegalArgumentException("Giá mua ngay phải lớn hơn giá sàn hoặc giá khởi điểm.");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = req.getStartTime() != null ? req.getStartTime() : now;
        AuctionStatus initialStatus = startTime.isAfter(now.plusSeconds(30)) ? AuctionStatus.SCHEDULED : AuctionStatus.ACTIVE;
        if (initialStatus == AuctionStatus.ACTIVE && startTime.isBefore(now)) {
            startTime = now;
        }

        int durationDays = req.getDurationDays() > 0 ? req.getDurationDays() : 3;
        LocalDateTime endTime = startTime.plusDays(durationDays);

        Auction auction = new Auction();
        auction.setItemId(item.getId());
        auction.setStartingPrice(req.getStartingPrice());
        auction.setStepPrice(req.getStepPrice());
        auction.setReservePrice(req.getReservePrice());
        auction.setBuyItNowPrice(req.getBuyItNowPrice());
        auction.setCurrentPrice(req.getStartingPrice());
        auction.setBidCount(0);
        auction.setReserveMet(false);
        auction.setStartTime(startTime);
        auction.setEndTime(endTime);
        auction.setStatus(initialStatus);

        Auction saved = auctionRepository.save(auction);
        auditLogFacade.log("AUCTION_CREATED", sellerId, "Auction", saved.getId(),
                "Tạo phiên đấu giá cho sản phẩm #" + item.getId() + " với giá khởi điểm " + req.getStartingPrice());

        return saved;
    }

    @Transactional(readOnly = true)
    public AuctionDetailDto getAuctionDetail(Long auctionId, Long currentUserId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Phiên đấu giá không tồn tại: " + auctionId));

        Item item = itemRepository.findById(auction.getItemId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy sản phẩm của phiên đấu giá"));

        Category category = categoryRepository.findById(item.getCategoryId()).orElse(null);
        User seller = userRepository.findById(item.getSellerId()).orElse(null);

        List<ItemImage> images = itemImageRepository.findByItemIdOrderByDisplayOrderAsc(item.getId());
        List<String> imageUrls = images.stream().map(ItemImage::getImageUrl).collect(Collectors.toList());

        AuctionDetailDto dto = new AuctionDetailDto();
        dto.setId(auction.getId());
        dto.setItemId(item.getId());
        dto.setItemTitle(item.getTitle());
        dto.setItemDescription(item.getDescription());
        dto.setItemCondition(item.getItemCondition());
        dto.setMainImageUrl(item.getMainImageUrl());
        dto.setImageUrls(imageUrls);
        dto.setCategoryId(item.getCategoryId());
        dto.setCategoryName(category != null ? category.getName() : "Khác");
        dto.setSellerId(item.getSellerId());
        dto.setSellerUsername(seller != null ? seller.getUsername() : "seller");

        dto.setStartingPrice(auction.getStartingPrice());
        dto.setStepPrice(auction.getStepPrice());
        dto.setCurrentPrice(auction.getCurrentPrice());
        dto.setBuyItNowPrice(auction.getBuyItNowPrice());
        dto.setReserveMet(auction.isReserveMet()); // ONLY boolean flag, never reveal secret reserve price!
        dto.setBidCount(auction.getBidCount());
        dto.setStartTime(auction.getStartTime());
        dto.setEndTime(auction.getEndTime());
        dto.setStatus(auction.getStatus());

        if (auction.getHighestBidderId() != null) {
            userRepository.findById(auction.getHighestBidderId()).ifPresent(bidder -> {
                dto.setHighestBidderMasked(StringUtil.maskUsername(bidder.getUsername()));
            });
        }

        // Buy It Now available rule:
        boolean buyItNowAvailable = auction.getBuyItNowPrice() != null
                && auction.getStatus() == AuctionStatus.ACTIVE
                && !auction.isReserveMet()
                && (auction.getReservePrice() != null || auction.getBidCount() == 0);
        dto.setBuyItNowAvailable(buyItNowAvailable);

        boolean isSeller = currentUserId != null && currentUserId.equals(item.getSellerId());
        dto.setUserIsSeller(isSeller);
        dto.setCanCancel(isSeller && auction.getBidCount() == 0 && (auction.getStatus() == AuctionStatus.ACTIVE || auction.getStatus() == AuctionStatus.SCHEDULED));
        dto.setUserIsHighestBidder(currentUserId != null && currentUserId.equals(auction.getHighestBidderId()));

        // Bid history:
        List<Bid> bids = bidRepository.findByAuctionIdOrderByBidTimeDesc(auction.getId());
        Map<Long, String> maskedCache = new HashMap<>();
        List<BidHistoryDto> historyList = new ArrayList<>();
        for (Bid b : bids) {
            String maskedName = maskedCache.computeIfAbsent(b.getBidderId(), uid -> {
                return userRepository.findById(uid)
                        .map(u -> StringUtil.maskUsername(u.getUsername()))
                        .orElse("***");
            });
            historyList.add(new BidHistoryDto(b.getId(), b.getBidderId(), maskedName, b.getBidAmount(), b.getBidTime(), b.isAutoBid()));
        }
        dto.setBidHistory(historyList);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<AuctionResponseDto> searchAuctions(String keyword, Long categoryId, Long minPrice, Long maxPrice, String sortBy) {
        List<Auction> activeAuctions = auctionRepository.findByStatusOrderByCreatedAtDesc(AuctionStatus.ACTIVE);
        LocalDateTime now = LocalDateTime.now();

        List<AuctionResponseDto> results = new ArrayList<>();
        for (Auction a : activeAuctions) {
            if (a.getEndTime().isBefore(now)) {
                continue;
            }
            Item item = itemRepository.findById(a.getItemId()).orElse(null);
            if (item == null) continue;

            if (categoryId != null && !item.getCategoryId().equals(categoryId)) {
                continue;
            }
            if (keyword != null && !keyword.isBlank()) {
                String kw = keyword.toLowerCase().trim();
                boolean matchesTitle = item.getTitle() != null && item.getTitle().toLowerCase().contains(kw);
                boolean matchesDesc = item.getDescription() != null && item.getDescription().toLowerCase().contains(kw);
                if (!matchesTitle && !matchesDesc) {
                    continue;
                }
            }
            if (minPrice != null && a.getCurrentPrice() < minPrice) {
                continue;
            }
            if (maxPrice != null && a.getCurrentPrice() > maxPrice) {
                continue;
            }

            Category category = categoryRepository.findById(item.getCategoryId()).orElse(null);

            AuctionResponseDto dto = new AuctionResponseDto();
            dto.setId(a.getId());
            dto.setItemId(item.getId());
            dto.setItemTitle(item.getTitle());
            dto.setItemImageUrl(item.getMainImageUrl());
            dto.setCategoryName(category != null ? category.getName() : "Khác");
            dto.setStartingPrice(a.getStartingPrice());
            dto.setStepPrice(a.getStepPrice());
            dto.setCurrentPrice(a.getCurrentPrice());
            dto.setBuyItNowPrice(a.getBuyItNowPrice());
            dto.setReserveMet(a.isReserveMet());
            dto.setBidCount(a.getBidCount());
            dto.setStartTime(a.getStartTime());
            dto.setEndTime(a.getEndTime());
            dto.setStatus(a.getStatus());
            dto.setBuyItNowAvailable(a.getBuyItNowPrice() != null && !a.isReserveMet() && (a.getReservePrice() != null || a.getBidCount() == 0));

            results.add(dto);
        }

        // Sorting
        if ("endingSoon".equalsIgnoreCase(sortBy)) {
            results.sort(Comparator.comparing(AuctionResponseDto::getEndTime));
        } else if ("priceAsc".equalsIgnoreCase(sortBy)) {
            results.sort(Comparator.comparingLong(AuctionResponseDto::getCurrentPrice));
        } else if ("priceDesc".equalsIgnoreCase(sortBy)) {
            results.sort(Comparator.comparingLong(AuctionResponseDto::getCurrentPrice).reversed());
        } else {
            // default: newest
            results.sort(Comparator.comparing(AuctionResponseDto::getStartTime).reversed());
        }

        return results;
    }

    @Transactional
    public Order executeBuyItNow(Long auctionId, Long buyerId) {
        Auction auction = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Phiên đấu giá không tồn tại: " + auctionId));

        if (auction.getStatus() != AuctionStatus.ACTIVE || auction.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Phiên đấu giá đã kết thúc hoặc không còn khả dụng để mua ngay.");
        }

        Item item = itemRepository.findById(auction.getItemId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy thông tin sản phẩm"));

        if (item.getSellerId().equals(buyerId)) {
            throw new IllegalArgumentException("Bạn không thể mua ngay sản phẩm do chính mình đăng bán.");
        }

        if (auction.getBuyItNowPrice() == null) {
            throw new IllegalStateException("Phiên đấu giá này không có tuỳ chọn Mua Ngay.");
        }

        boolean buyItNowAvailable = !auction.isReserveMet() && (auction.getReservePrice() != null || auction.getBidCount() == 0);
        if (!buyItNowAvailable) {
            throw new IllegalStateException("Tuỳ chọn Mua Ngay đã kết thúc do giá sàn đã đạt hoặc đã có lượt đấu giá.");
        }

        long buyItNowPrice = auction.getBuyItNowPrice();
        auction.setStatus(AuctionStatus.ENDED_SOLD);
        auction.setCurrentPrice(buyItNowPrice);
        auction.setHighestBidderId(buyerId);
        auctionRepository.save(auction);

        Order order = orderService.createOrder(auction.getId(), buyerId, item.getSellerId(), PurchaseType.BUY_IT_NOW, buyItNowPrice, 0L);

        // Send notifications
        notificationFacade.sendNotification(buyerId, "AUCTION_WON", "Mua ngay thành công!",
                "Bạn đã mua ngay thành công sản phẩm '" + item.getTitle() + "' với mức giá " + buyItNowPrice + " ₫. Vui lòng hoàn tất thanh toán trong 72 giờ.",
                "/orders/" + order.getId());

        notificationFacade.sendNotification(item.getSellerId(), "ITEM_SOLD", "Sản phẩm đã được mua ngay!",
                "Sản phẩm '" + item.getTitle() + "' của bạn đã được mua ngay với giá " + buyItNowPrice + " ₫.",
                "/orders/" + order.getId());

        auditLogFacade.log("AUCTION_BUY_IT_NOW", buyerId, "Auction", auction.getId(),
                "Người dùng #" + buyerId + " mua ngay với mức giá " + buyItNowPrice);

        return order;
    }

    @Transactional
    public void cancelAuction(Long auctionId, Long sellerId) {
        Auction auction = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Phiên đấu giá không tồn tại: " + auctionId));

        Item item = itemRepository.findById(auction.getItemId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy thông tin sản phẩm"));

        if (!item.getSellerId().equals(sellerId)) {
            throw new IllegalArgumentException("Bạn không có quyền huỷ phiên đấu giá này.");
        }

        if (auction.getBidCount() > 0) {
            throw new IllegalStateException("Không thể huỷ phiên đấu giá khi đã có người đặt giá.");
        }

        if (auction.getStatus() != AuctionStatus.ACTIVE && auction.getStatus() != AuctionStatus.SCHEDULED) {
            throw new IllegalStateException("Chỉ có thể huỷ phiên đấu giá đang diễn ra hoặc đã lên lịch.");
        }

        auction.setStatus(AuctionStatus.CANCELED);
        auctionRepository.save(auction);

        auditLogFacade.log("AUCTION_CANCELED", sellerId, "Auction", auction.getId(),
                "Người bán #" + sellerId + " đã huỷ phiên đấu giá #" + auction.getId());
    }

    @Transactional(readOnly = true)
    public Optional<Auction> findById(Long id) {
        return auctionRepository.findById(id);
    }
}
