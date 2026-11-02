package com.auction.order.service;

import com.auction.auction.entity.Auction;
import com.auction.auction.entity.AuctionStatus;
import com.auction.auction.repository.AuctionRepository;
import com.auction.audit.facade.AuditLogFacade;
import com.auction.catalog.entity.Item;
import com.auction.catalog.repository.ItemRepository;
import com.auction.common.util.MoneyUtil;
import com.auction.notification.facade.NotificationFacade;
import com.auction.order.dto.OrderResponseDto;
import com.auction.order.entity.Order;
import com.auction.order.entity.OrderStatus;
import com.auction.order.entity.PurchaseType;
import com.auction.order.repository.OrderRepository;
import com.auction.user.entity.User;
import com.auction.user.repository.UserRepository;
import com.auction.user.service.UserService;
import com.auction.wallet.facade.WalletFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AuctionRepository auctionRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final WalletFacade walletFacade;
    private final UserService userService;
    private final NotificationFacade notificationFacade;
    private final AuditLogFacade auditLogFacade;

    public OrderService(OrderRepository orderRepository) {
        this(orderRepository, null, null, null, null, null, null, null);
    }

    public OrderService(
            OrderRepository orderRepository,
            AuctionRepository auctionRepository,
            ItemRepository itemRepository,
            UserRepository userRepository,
            WalletFacade walletFacade,
            UserService userService,
            NotificationFacade notificationFacade,
            AuditLogFacade auditLogFacade) {
        this.orderRepository = orderRepository;
        this.auctionRepository = auctionRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.walletFacade = walletFacade;
        this.userService = userService;
        this.notificationFacade = notificationFacade;
        this.auditLogFacade = auditLogFacade;
    }

    @Transactional
    public Order createOrder(Long auctionId, Long buyerId, Long sellerId, PurchaseType purchaseType, long finalPrice, long shippingFee) {
        Order order = new Order();
        order.setAuctionId(auctionId);
        order.setBuyerId(buyerId);
        order.setSellerId(sellerId);
        order.setPurchaseType(purchaseType);
        order.setFinalPrice(finalPrice);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(finalPrice + shippingFee);
        order.setStatus(OrderStatus.AWAITING_PAYMENT);
        order.setPaymentDeadline(LocalDateTime.now().plusHours(72));
        return orderRepository.save(order);
    }

    @Transactional
    public Order checkoutAndPay(Long orderId, Long buyerId, String shippingAddress) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        if (!order.getBuyerId().equals(buyerId)) {
            throw new IllegalArgumentException("Bạn không phải người mua của đơn hàng này.");
        }

        if (order.getStatus() != OrderStatus.AWAITING_PAYMENT) {
            throw new IllegalStateException("Đơn hàng không ở trạng thái chờ thanh toán (Hiện tại: " + order.getStatus() + ").");
        }

        if (LocalDateTime.now().isAfter(order.getPaymentDeadline())) {
            throw new IllegalStateException("Đơn hàng đã quá hạn thanh toán.");
        }

        if (shippingAddress == null || shippingAddress.trim().isBlank()) {
            throw new IllegalArgumentException("Vui lòng cung cấp địa chỉ nhận hàng.");
        }

        // Deduct from buyer wallet using idempotency key
        String idempotencyKey = "ORDER_PAY_" + order.getId() + "_" + UUID.randomUUID();
        walletFacade.payOrder(buyerId, order.getId(), order.getTotalAmount(), idempotencyKey);

        order.setShippingAddress(shippingAddress.trim());
        order.setStatus(OrderStatus.PAID);
        Order updated = orderRepository.save(order);

        // Notify seller to prepare shipment
        notificationFacade.sendNotification(order.getSellerId(), "ORDER_PAID", "Đơn hàng đã thanh toán thành công",
                "Đơn hàng #" + order.getId() + " đã được người mua thanh toán " + MoneyUtil.formatVnd(order.getTotalAmount())
                        + ". Vui lòng chuẩn bị và gửi hàng theo địa chỉ: " + shippingAddress,
                "/orders/" + order.getId());

        auditLogFacade.log("ORDER_PAID", buyerId, "Order", order.getId(),
                "Thanh toán đơn hàng thành công qua ví: " + order.getTotalAmount());

        return updated;
    }

    @Transactional
    public Order shipOrder(Long orderId, Long sellerId, String trackingNumber) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        if (!order.getSellerId().equals(sellerId)) {
            throw new IllegalArgumentException("Bạn không phải người bán của đơn hàng này.");
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Chỉ có thể giao hàng cho đơn đã thanh toán.");
        }

        if (trackingNumber == null || trackingNumber.trim().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập mã vận đơn.");
        }

        order.setTrackingNumber(trackingNumber.trim());
        order.setStatus(OrderStatus.SHIPPED);
        Order updated = orderRepository.save(order);

        // Notify buyer
        notificationFacade.sendNotification(order.getBuyerId(), "GENERAL", "Đơn hàng đang được vận chuyển",
                "Đơn hàng #" + order.getId() + " đã được người bán gửi đi. Mã vận đơn: " + trackingNumber,
                "/orders/" + order.getId());

        auditLogFacade.log("ORDER_SHIPPED", sellerId, "Order", order.getId(),
                "Người bán cập nhật mã vận đơn: " + trackingNumber);

        return updated;
    }

    @Transactional
    public Order confirmReceived(Long orderId, Long buyerId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        if (!order.getBuyerId().equals(buyerId)) {
            throw new IllegalArgumentException("Bạn không phải người mua của đơn hàng này.");
        }

        if (order.getStatus() != OrderStatus.SHIPPED && order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Trạng thái đơn hàng không hợp lệ để xác nhận nhận hàng.");
        }

        order.setStatus(OrderStatus.COMPLETED);
        Order updated = orderRepository.save(order);

        // Payout to seller (Day 23: 5% fee retained for platform, 95% to seller)
        String idempotencyKey = "PAYOUT_" + order.getId() + "_" + UUID.randomUUID();
        walletFacade.payoutSeller(order.getSellerId(), order.getId(), order.getFinalPrice(), 0.05, idempotencyKey);

        notificationFacade.sendNotification(order.getSellerId(), "GENERAL", "Đơn hàng hoàn tất & Đã giải ngân",
                "Người mua đã xác nhận nhận hàng đơn #" + order.getId()
                        + ". Tiền bán hàng (sau khi trừ 5% phí sàn) đã được giải ngân vào ví của bạn.",
                "/wallet");

        auditLogFacade.log("ORDER_COMPLETED", buyerId, "Order", order.getId(),
                "Người mua xác nhận nhận hàng thành công. Giải ngân đơn hàng #" + order.getId());

        return updated;
    }

    @Transactional
    public void cancelUnpaidOrder(Long orderId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        if (order.getStatus() != OrderStatus.AWAITING_PAYMENT) {
            return;
        }

        order.setStatus(OrderStatus.CANCELLED_UNPAID);
        orderRepository.save(order);

        // Strike penalty (Day 20)
        userService.recordUnpaidStrike(order.getBuyerId());

        notificationFacade.sendNotification(order.getBuyerId(), "GENERAL", "Bị phạt vi phạm bùng hàng (Strike)",
                "Đơn hàng #" + order.getId() + " đã bị huỷ do quá hạn 72 giờ không thanh toán. Tài khoản của bạn bị phạt 1 điểm gậy vi phạm bùng hàng.",
                "/orders/" + order.getId());

        notificationFacade.sendNotification(order.getSellerId(), "GENERAL", "Đơn hàng bị huỷ do người mua không thanh toán",
                "Đơn hàng #" + order.getId() + " đã bị huỷ do người mua quá hạn thanh toán. Bạn có thể mở bán lại (Relist) sản phẩm bất cứ lúc nào.",
                "/orders/" + order.getId());

        auditLogFacade.log("ORDER_CANCELLED_UNPAID", order.getBuyerId(), "Order", order.getId(),
                "Huỷ đơn hàng do quá hạn thanh toán 72h. Phạt 1 strike cho buyer #" + order.getBuyerId());
    }

    /**
     * Mở bán lại phiên đấu giá (Relist - Day 20 / BR-16)
     */
    @Transactional
    public Auction relistAuction(Long auctionId, Long sellerId) {
        Auction oldAuction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiên đấu giá: " + auctionId));

        Item item = itemRepository.findById(oldAuction.getItemId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy sản phẩm"));

        if (!item.getSellerId().equals(sellerId)) {
            throw new IllegalArgumentException("Bạn không có quyền đăng lại sản phẩm này.");
        }

        // Tạo phiên mới từ sản phẩm cũ
        Auction newAuction = new Auction();
        newAuction.setItemId(item.getId());
        newAuction.setStartingPrice(oldAuction.getStartingPrice());
        newAuction.setStepPrice(oldAuction.getStepPrice());
        newAuction.setReservePrice(oldAuction.getReservePrice());
        newAuction.setBuyItNowPrice(oldAuction.getBuyItNowPrice());
        newAuction.setCurrentPrice(oldAuction.getStartingPrice());
        newAuction.setBidCount(0);
        newAuction.setReserveMet(false);
        newAuction.setStartTime(LocalDateTime.now());
        newAuction.setEndTime(LocalDateTime.now().plusDays(3));
        newAuction.setStatus(AuctionStatus.ACTIVE);

        Auction saved = auctionRepository.save(newAuction);
        auditLogFacade.log("AUCTION_RELISTED", sellerId, "Auction", saved.getId(),
                "Người bán #" + sellerId + " mở bán lại món hàng #" + item.getId() + " từ phiên cũ #" + auctionId);

        return saved;
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderDetail(Long orderId, Long currentUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        Auction auction = auctionRepository.findById(order.getAuctionId()).orElse(null);
        Item item = (auction != null) ? itemRepository.findById(auction.getItemId()).orElse(null) : null;
        User buyer = userRepository.findById(order.getBuyerId()).orElse(null);
        User seller = userRepository.findById(order.getSellerId()).orElse(null);

        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setAuctionId(order.getAuctionId());
        dto.setItemId(item != null ? item.getId() : null);
        dto.setItemTitle(item != null ? item.getTitle() : ("Phiên #" + order.getAuctionId()));
        dto.setItemImageUrl(item != null ? item.getMainImageUrl() : null);

        dto.setBuyerId(order.getBuyerId());
        dto.setBuyerUsername(buyer != null ? buyer.getUsername() : "buyer");
        dto.setSellerId(order.getSellerId());
        dto.setSellerUsername(seller != null ? seller.getUsername() : "seller");

        dto.setPurchaseType(order.getPurchaseType());
        dto.setFinalPrice(order.getFinalPrice());
        dto.setShippingFee(order.getShippingFee());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setStatus(order.getStatus());
        dto.setPaymentDeadline(order.getPaymentDeadline());
        dto.setCreatedAt(order.getCreatedAt());

        boolean isBuyer = currentUserId != null && currentUserId.equals(order.getBuyerId());
        boolean isSeller = currentUserId != null && currentUserId.equals(order.getSellerId());
        dto.setBuyer(isBuyer);
        dto.setSeller(isSeller);

        dto.setCanPay(isBuyer && order.getStatus() == OrderStatus.AWAITING_PAYMENT && LocalDateTime.now().isBefore(order.getPaymentDeadline()));
        dto.setCanShip(isSeller && order.getStatus() == OrderStatus.PAID);
        dto.setCanConfirmReceived(isBuyer && order.getStatus() == OrderStatus.SHIPPED);
        dto.setCanReview(order.getStatus() == OrderStatus.COMPLETED);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getMyPurchases(Long buyerId) {
        List<Order> orders = orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId);
        List<OrderResponseDto> result = new ArrayList<>();
        for (Order o : orders) {
            result.add(getOrderDetail(o.getId(), buyerId));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getMySales(Long sellerId) {
        List<Order> orders = orderRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
        List<OrderResponseDto> result = new ArrayList<>();
        for (Order o : orders) {
            result.add(getOrderDetail(o.getId(), sellerId));
        }
        return result;
    }
}
