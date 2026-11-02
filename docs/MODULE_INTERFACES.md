# 🧩 THIẾT KẾ GIAO DIỆN MODULE & SỰ KIỆN MIỀN (MODULE INTERFACES & EVENTS)

> **Dự án:** Hệ Thống Đấu Giá Trực Tuyến (Cơ chế Đấu giá Tự động)  
> **Căn cứ:** `docs/PLAN.md` (Ngày 4)  
> **Quy tắc:** Module chỉ tương tác qua Facade hoặc Event; không được phụ thuộc trực tiếp vào Repository của module khác.

---

## 1. DANH SÁCH FACADE GIỮA CÁC MODULE

### 1.1. `WalletFacade` (Module `wallet`)
Trách nhiệm: Cung cấp API quản lý số dư và giao dịch tài chính cho các module khác.
```java
public interface WalletFacade {
    // Lấy số dư khả dụng
    long getAvailableBalance(Long userId);

    // Nạp tiền
    void topUp(Long userId, long amount, String idempotencyKey);

    // Rút tiền
    void withdraw(Long userId, long amount, String bankAccount, String idempotencyKey);

    // Khấu trừ thanh toán đơn hàng (giữ tiền vào bảo chứng Escrow)
    void payOrder(Long buyerId, Long orderId, long amount, String idempotencyKey);

    // Giải ngân tiền bán hàng cho Seller sau khi trừ phí hoa hồng sàn
    void payoutSeller(Long sellerId, Long orderId, long finalPrice, double feePercent, String idempotencyKey);

    // Hoàn tiền đơn hàng cho Buyer
    void refundOrder(Long buyerId, Long orderId, long amount, String idempotencyKey);
}
```

### 1.2. `NotificationFacade` (Module `notification`)
Trách nhiệm: Gửi thông báo trong hệ thống cho người dùng khi xảy ra các sự kiện nghiệp vụ.
```java
public interface NotificationFacade {
    void sendNotification(Long userId, String type, String title, String content, String actionUrl);
    int getUnreadCount(Long userId);
    void markAsRead(Long notificationId, Long userId);
}
```

### 1.3. `SystemConfigFacade` (Module `system`)
Trách nhiệm: Đọc cấu hình sàn và tính toán bước giá tối thiểu.
```java
public interface SystemConfigFacade {
    int getPlatformFeePercent();
    int getPaymentDeadlineHours();
    int getMaxUnpaidStrikes();
    long calculateMinimumNextBid(long currentPrice);
}
```

### 1.4. `AuditLogFacade` (Module `audit`)
Trách nhiệm: Ghi nhận vết kiểm toán bất biến.
```java
public interface AuditLogFacade {
    void log(String action, Long userId, String entityName, Long entityId, String details);
}
```

---

## 2. DANH SÁCH DOMAIN EVENTS (SPRING APPLICATION EVENTS)

Hệ thống sử dụng cơ chế phát sự kiện bất đồng bộ qua `ApplicationEventPublisher`:

| Tên Event | Mô tả kích hoạt | Dữ liệu mang theo (Payload) | Module lắng nghe |
| :--- | :--- | :--- | :--- |
| **`AuctionCreatedEvent`** | Khi Seller tạo phiên thành công | `auctionId`, `sellerId`, `title`, `startTime` | `audit`, `notification` |
| **`ProxyBidPlacedEvent`** | Khi người mua gửi mức giá trần mới | `auctionId`, `bidderId`, `newCurrentPrice`, `highestBidderId` | `audit`, `realtime` |
| **`OutbidEvent`** | Khi người dẫn đầu bị đối thủ vượt giá | `auctionId`, `previousBidderId`, `newPrice` | `notification`, `realtime` |
| **`AuctionSoldEvent`** | Khi phiên kết thúc thành công hoặc Buy It Now | `auctionId`, `winnerId`, `sellerId`, `finalPrice` | `order`, `notification`, `realtime` |
| **`AuctionUnsoldEvent`** | Khi phiên kết thúc không thành | `auctionId`, `sellerId`, `reason` | `notification`, `realtime` |
| **`OrderPaidEvent`** | Khi người mua thanh toán tiền hàng | `orderId`, `buyerId`, `sellerId`, `totalAmount` | `notification`, `audit` |
| **`OrderDefaultedEvent`**| Khi người mua quá hạn thanh toán | `orderId`, `buyerId`, `sellerId` | `user`, `notification`, `auction` |

