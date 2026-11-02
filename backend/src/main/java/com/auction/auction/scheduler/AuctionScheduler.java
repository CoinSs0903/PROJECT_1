package com.auction.auction.scheduler;

import com.auction.auction.entity.Auction;
import com.auction.auction.entity.AuctionStatus;
import com.auction.auction.repository.AuctionRepository;
import com.auction.audit.facade.AuditLogFacade;
import com.auction.catalog.entity.Item;
import com.auction.catalog.repository.ItemRepository;
import com.auction.common.util.MoneyUtil;
import com.auction.notification.facade.NotificationFacade;
import com.auction.order.entity.Order;
import com.auction.order.entity.PurchaseType;
import com.auction.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AuctionScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuctionScheduler.class);

    private final AuctionRepository auctionRepository;
    private final ItemRepository itemRepository;
    private final OrderService orderService;
    private final NotificationFacade notificationFacade;
    private final AuditLogFacade auditLogFacade;

    public AuctionScheduler(
            AuctionRepository auctionRepository,
            ItemRepository itemRepository,
            OrderService orderService,
            NotificationFacade notificationFacade,
            AuditLogFacade auditLogFacade) {
        this.auctionRepository = auctionRepository;
        this.itemRepository = itemRepository;
        this.orderService = orderService;
        this.notificationFacade = notificationFacade;
        this.auditLogFacade = auditLogFacade;
    }

    @Scheduled(fixedDelay = 5000)
    public void processExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> expired = auctionRepository.findExpiredActiveAuctions(now);
        for (Auction a : expired) {
            try {
                closeSingleAuction(a.getId(), now);
            } catch (Exception e) {
                log.error("Lỗi khi tự động kết thúc phiên đấu giá #{}: {}", a.getId(), e.getMessage());
            }
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void processScheduledAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> dueScheduled = auctionRepository.findDueScheduledAuctions(now);
        for (Auction a : dueScheduled) {
            try {
                activateSingleAuction(a.getId(), now);
            } catch (Exception e) {
                log.error("Lỗi khi tự động kích hoạt phiên đấu giá #{}: {}", a.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    public void closeSingleAuction(Long auctionId, LocalDateTime now) {
        Auction auction = auctionRepository.findByIdForUpdate(auctionId).orElse(null);
        if (auction == null || auction.getStatus() != AuctionStatus.ACTIVE || auction.getEndTime().isAfter(now)) {
            return;
        }

        Item item = itemRepository.findById(auction.getItemId()).orElse(null);
        String itemTitle = (item != null) ? item.getTitle() : ("#" + auction.getItemId());
        Long sellerId = (item != null) ? item.getSellerId() : 0L;

        boolean isSold = auction.getBidCount() > 0
                && auction.getHighestBidderId() != null
                && (auction.getReservePrice() == null || auction.isReserveMet());

        if (isSold) {
            auction.setStatus(AuctionStatus.ENDED_SOLD);
            auctionRepository.save(auction);

            Order order = orderService.createOrder(
                    auction.getId(),
                    auction.getHighestBidderId(),
                    sellerId,
                    PurchaseType.AUCTION_WIN,
                    auction.getCurrentPrice(),
                    0L
            );

            // Notify winner
            notificationFacade.sendNotification(
                    auction.getHighestBidderId(),
                    "AUCTION_WON",
                    "Chúc mừng! Bạn đã thắng đấu giá",
                    "Bạn đã thắng đấu giá sản phẩm '" + itemTitle + "' với mức giá "
                            + MoneyUtil.formatVnd(auction.getCurrentPrice())
                            + ". Vui lòng hoàn tất thanh toán trong 72 giờ.",
                    "/orders/" + order.getId()
            );

            // Notify seller
            notificationFacade.sendNotification(
                    sellerId,
                    "ITEM_SOLD",
                    "Sản phẩm đấu giá thành công!",
                    "Sản phẩm '" + itemTitle + "' của bạn đã được bán thành công cho người thắng với giá "
                            + MoneyUtil.formatVnd(auction.getCurrentPrice()) + ".",
                    "/orders/" + order.getId()
            );

            auditLogFacade.log("AUCTION_ENDED_SOLD", auction.getHighestBidderId(), "Auction", auction.getId(),
                    "Phiên đấu giá kết thúc có người thắng: Giá " + auction.getCurrentPrice());

            log.info("Phiên đấu giá #{} đã kết thúc có người thắng. Đơn hàng #{} đã được tạo.", auction.getId(), order.getId());
        } else {
            auction.setStatus(AuctionStatus.ENDED_UNSOLD);
            auctionRepository.save(auction);

            notificationFacade.sendNotification(
                    sellerId,
                    "AUCTION_UNSOLD",
                    "Phiên đấu giá kết thúc không người mua",
                    "Sản phẩm '" + itemTitle + "' kết thúc nhưng không có người mua hoặc chưa đạt mức giá sàn.",
                    "/auctions/" + auction.getId()
            );

            auditLogFacade.log("AUCTION_ENDED_UNSOLD", sellerId, "Auction", auction.getId(),
                    "Phiên đấu giá kết thúc không bán được (không có bid hoặc không đạt giá sàn)");

            log.info("Phiên đấu giá #{} đã kết thúc mà không bán được.", auction.getId());
        }
    }

    @Transactional
    public void activateSingleAuction(Long auctionId, LocalDateTime now) {
        Auction auction = auctionRepository.findByIdForUpdate(auctionId).orElse(null);
        if (auction == null || auction.getStatus() != AuctionStatus.SCHEDULED || auction.getStartTime().isAfter(now)) {
            return;
        }

        auction.setStatus(AuctionStatus.ACTIVE);
        auctionRepository.save(auction);

        auditLogFacade.log("AUCTION_ACTIVATED", 0L, "Auction", auction.getId(),
                "Phiên đấu giá đã đến giờ bắt đầu và chuyển sang ACTIVE");

        log.info("Phiên đấu giá #{} đã chuyển sang ACTIVE.", auction.getId());
    }
}
