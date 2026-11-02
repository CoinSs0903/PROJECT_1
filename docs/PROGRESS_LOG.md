# PROGRESS LOG – Nhật Ký Tiến Độ Dự Án

## 1. Trạng Thái Module (Đã đồng bộ theo ERD mới 14 bảng - Cơ chế Đấu giá Tự động)

| Module | Trách nhiệm chính | Trạng Thái | Ngày Kế Hoạch | Ghi Chú |
| :--- | :--- | :--- | :---: | :--- |
| **môi trường** | Cài đặt JDK, XAMPP, cấu hình dự án | **Hoàn thành** | Ngày 0 | JDK 17, MySQL, Spring Boot 3, Flyway, Thymeleaf |
| **docs / db** | SRS, ERD mới 14 bảng, V1 migration, DECISIONS | **Hoàn thành** | Ngày 1 | ERD 14 bảng chuẩn C2C Proxy Bidding, migration sạch |
| **docs** | STATE_MACHINES, BUSINESS_RULES | **Hoàn thành** | Ngày 2 | 5 state machines Mermaid, 25 Business Rules & Scheduler |
| **docs** | SEQUENCES (UC-01 .. UC-04) | **Hoàn thành** | Ngày 3 | Listing, Proxy Bidding, Buy It Now, Auto Closer |
| **docs** | SEQUENCES (UC-05 .. UC-08), USE_CASES, MODULE_INTERFACES, API_MAP | **Hoàn thành** | Ngày 4 | Checkout/Escrow, Unpaid Strike, Relist, Map API & WebSocket |
| **common** | Giờ server, tiền VND, Exception, layout Bootstrap 5 | **Hoàn thành** | Ngày 5 | `BaseEntity`, `MoneyUtil`, `DateTimeUtil`, layout Thymeleaf + BS5 |
| **user** | Tài khoản, đăng nhập, phân quyền, hồ sơ cá nhân | **Hoàn thành** | Ngày 6 | Spring Security BCrypt, `User`, `UserService`, Form login/register |
| **wallet** | Ví tiền, giao dịch bất biến, nạp/rút, bảo chứng Escrow | **Hoàn thành** | Ngày 7 | `Wallet`, `Transaction`, `WalletFacade`, Idempotency key, Lock PESSIMISTIC |
| **system** | Cấu hình hệ thống (bước giá, phí sàn, hạn thanh toán) | **Hoàn thành** | Ngày 8 | `SystemConfig`, `SystemConfigService`, cache in-memory |
| **audit** | Nhật ký kiểm toán bất biến (append-only) | **Hoàn thành** | Ngày 8 | `AuditLog`, `V2__audit_logs.sql`, `AuditLogService` REQUIRES_NEW |
| **notification** | Thông báo trong ứng dụng, chuông thông báo | **Hoàn thành** | Ngày 9 | `Notification`, `NotificationFacade`, UI bell dropdown Navbar |
| **catalog** | Quản lý Danh mục (Categories) & Món hàng (Items) | **Hoàn thành** | Ngày 10 | `Category`, `Item`, `ItemImage`, `CatalogService`, UI quản lý món hàng |
| **auction** | Đăng bán món hàng & Thiết lập thông số đấu giá | **Hoàn thành** | Ngày 11, 12, 14, 15 | Tạo phiên, chi tiết, tìm kiếm, Scheduler, Buy It Now, Huỷ |
| **proxy_bidding** | Bộ máy đấu giá tự động (Proxy Bidding Engine) | **Hoàn thành** | Ngày 13 | Khóa PESSIMISTIC, tự động nhảy giá, đạt giá sàn, thông báo OUTBID |
| **order** | Đơn hàng trúng đấu giá, thanh toán, xử phạt bùng hàng | **Hoàn thành** | Ngày 18, 19, 20 | Checkout, thanh toán Ví bảo chứng, gậy bùng hàng, Relist |
| **realtime** | WebSocket STOMP, nhảy giá thời gian thực | **Hoàn thành** | Ngày 21, 22 | Spring STOMP over SockJS, `/topic/auctions/{id}`, live update |
| **payout** | Giải ngân tiền cho Seller sau khi trừ phí hoa hồng sàn | **Hoàn thành** | Ngày 23 | Thu 5% phí sàn vào SYSTEM_REVENUE, 95% giải ngân Seller |
| **complaint** | Tiếp nhận và giải quyết khiếu nại & Đánh giá uy tín | **Hoàn thành** | Ngày 24 | `Review` 1-5 sao, `Complaint` khiếu nại, Admin hoàn tiền |
| **admin** | Bảng điều khiển quản trị tổng hợp | **Hoàn thành** | Ngày 24, 25 | Thống kê doanh thu, quản lý vi phạm (khóa/mở khóa), tra cứu audit |
| **toàn hệ thống** | Rà soát bảo mật, demo data, nghiệm thu | **Hoàn thành** | Ngày 26 | Seed data 20 món hàng & tài khoản mẫu, 41/41 unit tests pass 100% |

---

## 2. Nợ Kỹ Thuật (Technical Debt)

| Mã | Nội Dung Làm Tạm | Module | Ngày Gốc | Ngày Dự Kiến Xử Lý | Trạng Thái |
| :--- | :--- | :--- | :--- | :--- | :--- |
| — | Chưa có | — | — | — | — |

---

## 3. Nhật Ký Chi Tiết Thực Hiện Đến Ngày 10

### Ngày 0 – Cài đặt môi trường & Khởi tạo dự án
- Hoàn thành thiết lập dự án Spring Boot, JDK 17, cấu hình kết nối MySQL và Flyway.

### Ngày 1 – Thiết kế ERD mới & Migration V1
- Hoàn thiện mô hình ERD mới tinh gọn 14 bảng tập trung 100% vào cơ chế đấu giá tự động Proxy Bidding, Reserve Price, Buy It Now, Relist, Order.
- File migration `V1__init.sql` khớp 100% với ERD mới.
- Cập nhật 13 quyết định thiết kế cốt lõi trong `docs/DECISIONS.md`.

### Ngày 2 – Sơ đồ trạng thái & Ma trận quy tắc nghiệp vụ
- Hoàn thiện `docs/STATE_MACHINES.md`: 5 State Machines Mermaid (`AuctionStateMachine`, `OrderStateMachine`, `WalletTransactionStateMachine`, `ComplaintStateMachine`, `UserStatusStateMachine`).
- Hoàn thiện `docs/BUSINESS_RULES.md`: 25 quy tắc nghiệp vụ chuẩn C2C và 5 tác vụ Scheduler tự động ngầm.

### Ngày 3 & Ngày 4 – Sequence Diagrams, Use Cases, Module Interfaces & API Map
- Soạn thảo `docs/SEQUENCES.md` gồm 6 luồng tuần tự: Listing, Proxy Bidding Engine, Buy It Now, Auction Closer & Order Generation, Order Checkout & Escrow, Unpaid Strike & Relist.
- Soạn thảo `docs/USE_CASES.md`: Đặc tả chi tiết cho Guest, Buyer, Seller, Admin.
- Soạn thảo `docs/MODULE_INTERFACES.md`: Thiết kế các Facade (`WalletFacade`, `NotificationFacade`, `SystemConfigFacade`, `AuditLogFacade`) và Domain Events.
- Soạn thảo `docs/API_MAP.md`: Danh mục đầy đủ REST API và WebSocket channels `/ws-auction`.

### Ngày 5 – Khung dự án, thành phần dùng chung, giao diện nền (`common`)
- Package structure chuẩn: `common`, `user`, `wallet`, `system`, `audit`, `notification`, `catalog`.
- Xây dựng `BaseEntity`, `ApiResponse<T>`, `ErrorCode`, `AppException`, `GlobalExceptionHandler`.
- Tiện ích: `MoneyUtil` (tính bước giá theo bậc BR-03, định dạng VND), `DateTimeUtil` (chuẩn múi giờ Asia/Ho_Chi_Minh), `StringUtil` (masking tên người dùng `u***1`).
- Layout Thymeleaf + Bootstrap 5: `layout/main.html`, `layout/navbar.html`, `layout/footer.html`, `layout/messages.html`, `index.html`, `error.html`.
- Kiểm thử unit test: `MoneyUtilTest` (3/3 pass), `StringUtilTest` (1/1 pass).

### Ngày 6 – Tài khoản, đăng nhập, phân quyền & Hồ sơ uy tín (`user`)
- Entity `User` khớp 100% bảng `users` trong ERD mới: `username`, `email`, `password_hash`, `full_name`, `phone_number`, `shipping_address`, `bank_name`, `bank_account_number`, `role`, `unpaid_strikes_count`, `status`.
- Spring Security: `SecurityConfig`, `CustomUserDetails`, `CustomUserDetailsService` mã hóa BCrypt.
- `UserService`: đăng ký tài khoản (tự động tạo ví), cập nhật hồ sơ, ghi nhận gậy bùng hàng.
- `AuthController` & `UserController`, giao diện `auth/login.html`, `auth/register.html`, `user/profile.html`.
- Kiểm thử unit test: `UserServiceTest` (3/3 pass).

### Ngày 7 – Ví tiền và giao dịch tài chính bảo chứng (`wallet`)
- Entity `Wallet` (`available_balance`, `wallet_type = USER / SYSTEM_REVENUE`) và `Transaction` (nhật ký biến động bất biến).
- `WalletRepository` với khóa bi quan `PESSIMISTIC_WRITE` chống Race Condition.
- `WalletService` implementing `WalletFacade`: nạp tiền, rút tiền, thanh toán đơn hàng, giải ngân cho người bán khấu trừ 5% phí sàn, hoàn tiền, chống trùng lặp với `idempotency_key`.
- `WalletController` & giao diện quản lý ví cá nhân `wallet/index.html`.
- Kiểm thử unit test: `WalletServiceTest` (4/4 pass).

### Ngày 8 – Cấu hình hệ thống & Nhật ký kiểm toán (`system`, `audit`)
- Entity `SystemConfig`, `SystemConfigRepository`, `SystemConfigService` / `SystemConfigFacade` cache cấu hình trong bộ nhớ.
- Migration `V2__audit_logs.sql`, Entity `AuditLog`, `AuditLogRepository`, `AuditLogService` / `AuditLogFacade` với propagation `REQUIRES_NEW`.

### Ngày 9 – Thông báo trong ứng dụng (`notification`)
- Entity `Notification` khớp bảng `notifications` trong ERD mới (`OUTBID`, `AUCTION_ENDING`, `AUCTION_WON`, `ITEM_SOLD`,...).
- `NotificationService` & `NotificationFacade`.
- `NotificationController` và giao diện `notification/index.html`, chuông thông báo trên `navbar.html`.

### Ngày 10 – Quản lý Danh mục (Categories) & Món hàng (Items) (`catalog`)
- Entity `Category` (cây danh mục, tự động khởi tạo danh mục mẫu), `Item` (thuộc về Seller, phân loại danh mục, tình trạng `NEW`/`LIKE_NEW`/`USED`), `ItemImage` (album ảnh).
- `CatalogService`, `ItemController`, `CatalogApiController`.
- Giao diện quản lý: `catalog/my-items.html` (danh sách món hàng), `catalog/item-form.html` (đăng món hàng mới), `catalog/item-detail.html` (chi tiết sản phẩm & album ảnh).
- Kiểm thử unit test: `CatalogServiceTest` (2/2 pass).

### Ngày 11 – Đăng bán & Thiết lập thông số phiên đấu giá (`auction`)
- Entity `AuctionStatus` (`ACTIVE`, `SCHEDULED`, `ENDED_SOLD`, `ENDED_UNSOLD`, `CANCELED`) và `Auction` khớp 100% bảng `auctions` trong ERD mới.
- `AuctionRepository` hỗ trợ khóa bi quan `findByIdForUpdate` (PESSIMISTIC_WRITE) và các query theo trạng thái, thời gian.
- `AuctionService.createAuction(...)`: Ràng buộc tính hợp lệ giá khởi điểm (>= 0), bước giá (> 0), giá sàn bảo lưu (>= starting_price), giá mua ngay (> reserve_price hoặc > starting_price), kiểm tra quyền sở hữu món hàng của Seller và kiểm tra chống trùng lặp phiên đang diễn ra.
- `AuctionController` & `AuctionApiController`: Form giao diện mở phiên đấu giá `auction/create.html` tích hợp trực tiếp từ trang chi tiết món hàng `catalog/item-detail.html`.

### Ngày 12 – Chi tiết phiên đấu giá công khai & Tìm kiếm, Lọc (`auction`)
- `AuctionDetailDto` & `AuctionResponseDto`: Hiển thị thông số phiên, thông tin sản phẩm, album ảnh, người bán, bộ đếm ngược thời gian trực tiếp (live countdown), số lượt đặt giá, ẩn danh người dẫn đầu theo quy tắc BR-24 (`u***1`).
- Tuyệt đối bảo mật: Chỉ hiển thị cờ boolean `isReserveMet`, không bao giờ để lộ con số `reserve_price` bí mật của người bán.
- Tìm kiếm & Lọc (`/auctions`, `/`): Tìm kiếm theo từ khóa (tên/mô tả), danh mục sản phẩm, khoảng giá và sắp xếp linh hoạt (`newest`, `endingSoon`, `priceAsc`, `priceDesc`).
- Giao diện chi tiết: `auction/detail.html`, danh sách phiên đấu giá: `auction/list.html` và hiển thị trực quan các phiên nổi bật trên `index.html`.

### Ngày 13 – Bộ máy đấu giá tự động Proxy Bidding Engine (`proxy_bidding`)
- Entity `ProxyBid` (bảng `proxy_bids`) và `Bid` (bảng `bids`).
- `ProxyBiddingService.processBid(auctionId, bidderId, maxAmount)`:
  - Khóa bi quan `findByIdForUpdate` trên phiên đấu giá để ngăn chặn hoàn toàn Race Condition và Deadlock.
  - Kiểm tra điều kiện: Người bán không được phép tự đặt giá, tài khoản người đặt phải ở trạng thái ACTIVE, mức giá tối đa phải >= giá hiện tại + bước giá tối thiểu.
  - Thuật toán Proxy Bidding:
    - Nếu là lượt đặt đầu tiên: Giá hiện tại xuất phát từ `startingPrice`. Nếu `maxAmount >= reservePrice`, giá lập tức nhảy lên mức giá sàn bảo lưu và đánh dấu `isReserveMet = true`.
    - Nếu người dẫn đầu nâng giá tối đa: Cập nhật `maxAmount` mới và tự động kích hoạt giá sàn nếu đạt ngưỡng.
    - Nếu người đặt mới trả giá cao hơn người dẫn đầu hiện tại: Người mới dẫn đầu, giá phiên nhảy lên $\min(\text{maxAmount}, \text{maxCũ} + \text{stepPrice})$, gửi thông báo `OUTBID` cho người dẫn đầu cũ.
    - Nếu người đặt mới trả giá thấp hơn hoặc bằng giá tối đa của người dẫn đầu: Người dẫn đầu tiếp tục giữ vị trí, giá phiên tự động nhảy lên $\min(\text{maxDẫnĐầu}, \text{maxMới} + \text{stepPrice})$, gửi thông báo `OUTBID` ngay lập tức cho người đặt mới.
- REST API `POST /api/auctions/{id}/bids` và form HTML `POST /auctions/{id}/bid`.

### Ngày 14 – Tác vụ quét tự động kết thúc phiên Auction Scheduler (`auction`)
- Cấu hình `@EnableScheduling` và thành phần `AuctionScheduler`:
  - Quét mỗi 5 giây các phiên hết hạn (`status = ACTIVE` và `endTime <= now`):
    - Nếu có lượt đặt giá và đạt giá sàn (hoặc không có giá sàn) $\rightarrow$ chuyển sang `ENDED_SOLD`, tự động tạo đơn hàng `Order` (trạng thái `AWAITING_PAYMENT`, hạn thanh toán 72 giờ), gửi thông báo `AUCTION_WON` cho Buyer và `ITEM_SOLD` cho Seller.
    - Nếu không có người đặt hoặc chưa đạt giá sàn $\rightarrow$ chuyển sang `ENDED_UNSOLD`, gửi thông báo `AUCTION_UNSOLD` cho Seller.
  - Quét mỗi 10 giây các phiên đã đến giờ bắt đầu (`status = SCHEDULED` và `startTime <= now`) $\rightarrow$ tự động kích hoạt chuyển sang `ACTIVE`.

### Ngày 15 – Mua ngay Buy It Now & Huỷ phiên đấu giá (`auction`)
- Tính năng Mua ngay Buy It Now (`executeBuyItNow`):
  - Cho phép người mua chốt đơn ngay lập tức với mức giá `buyItNowPrice`.
  - Khóa phiên, chuyển trạng thái sang `ENDED_SOLD`, sinh đơn hàng `Order` và gửi thông báo cho cả 2 bên.
  - Nút Mua ngay tự động ẩn khi phiên đấu giá đã đạt giá sàn hoặc đã có lượt đấu giá. Người bán không thể tự mua sản phẩm của chính mình.
- Tính năng Huỷ phiên đấu giá (`cancelAuction`):
  - Người bán chỉ có quyền huỷ phiên đấu giá khi chưa có bất kỳ lượt đặt giá nào (`bidCount == 0`). Nếu đã có người đặt giá, hệ thống chặn hành động huỷ để bảo vệ tính công bằng của phiên.
- Endpoints: `POST /auctions/{id}/buy-it-now`, `POST /auctions/{id}/cancel`, cùng các API JSON tương ứng.

### Ngày 16 & 17 – Trải nghiệm Đặt giá & Xử lý đồng thời (Concurrency Lock) (`proxy_bidding`)
- Tối ưu hóa khóa bi quan `findByIdForUpdate` (PESSIMISTIC_WRITE) đảm bảo an toàn tuyệt đối trước các đợt trả giá dồn dập ở những giây cuối (Last-second Sniping).
- Phản hồi tức thời với thông điệp rõ ràng khi bị vượt giá hoặc khi giá tự động của đối thủ chặn ngay lập tức.
- Giữ vững nguyên tắc bảo mật danh tính người tham gia (`u***1`) và che giấu hoàn toàn giá trần bí mật.

### Ngày 18 – Rút lại giá (Bid Retraction) & Khởi tạo Đơn hàng (`proxy_bidding`, `order`)
- Nghiệp vụ Rút lại giá theo chuẩn BR-14:
  - Chỉ cho phép rút giá khi thời gian phiên còn hơn 12 tiếng (`endTime > now + 12h`).
  - Khi rút giá: xoá bản ghi `ProxyBid`, tự động tính toán lại mức giá hiện tại và người dẫn đầu từ các bidder còn lại, gửi thông báo realtime.
- Tự động tạo bản ghi `Order` khi phiên kết thúc thành công (`AuctionWonEvent`).

### Ngày 19 – Đơn hàng, Thanh toán ví bảo chứng (Order Checkout & Payment) (`order`)
- `OrderService.checkoutAndPay(...)`:
  - Khóa bi quan đơn hàng, kiểm tra hạn thanh toán 72h.
  - Khấu trừ số dư ví người mua an toàn qua `WalletFacade.payOrder` có `idempotency_key`, chuyển trạng thái đơn hàng sang `PAID` (tiền được sàn giữ bảo chứng an toàn).
  - Gửi thông báo `ORDER_PAID` cho người bán chuẩn bị giao hàng.
- Giao diện quản lý đơn hàng: [order/index.html](file:///d:/PTPMHĐT/PROJECT_1/backend/src/main/resources/templates/order/index.html) và chi tiết đơn hàng [order/detail.html](file:///d:/PTPMHĐT/PROJECT_1/backend/src/main/resources/templates/order/detail.html).

### Ngày 20 – Đăng lại hàng (Relist), Chào giá & Xử phạt bùng hàng (`order`, `auction`)
- Tự động hủy đơn hàng quá hạn thanh toán 72h (`CANCELLED_UNPAID`) qua `AuctionScheduler`.
- Phạt 1 điểm vi phạm bùng hàng (**Unpaid Item Strike**) cho người mua vi phạm. Tích lũy đủ 3 gậy tự động khóa quyền tham gia đấu giá.
- Chức năng **Relist** 1-click cho người bán: Tái sử dụng `item_id` tạo phiên đấu giá mới dễ dàng khi người mua bùng hàng hoặc phiên trước không đạt giá sàn.

### Ngày 21 & 22 – WebSocket STOMP và Đồng bộ giá thời gian thực (`realtime`)
- Cấu hình Spring STOMP over SockJS: Endpoint `/ws-auction`, message broker `/topic` và `/queue`.
- `AuctionRealtimeNotifier` phát sự kiện `BidUpdateEvent` lên kênh `/topic/auctions/{id}` ngay khi giá thay đổi hoặc người dẫn đầu mới xuất hiện.
- Giao diện phòng đấu giá trên client tự động nhảy số tiền hiển thị theo chuẩn VND với hiệu ứng nhấp nháy xanh/đỏ mà không cần tải lại trang.

### Ngày 23 – Hoàn tất đơn hàng & Giải ngân ví Người bán (Payout & Escrow) (`order`, `wallet`)
- Người mua bấm "Đã nhận hàng thành công" $\rightarrow$ Đơn hàng chuyển sang `COMPLETED`.
- Hệ thống tự động thu 5% phí dịch vụ sàn vào ví doanh thu `SYSTEM_REVENUE`.
- 95% tiền bán hàng được giải ngân tức thì vào ví khả dụng của Người bán (`available_balance`).
- Ghi nhận đầy đủ biến động số dư vào nhật ký kiểm toán tài chính `transactions`.

### Ngày 24 – Đánh giá uy tín & Trung tâm khiếu nại (`complaint`)
- Entity `Review` (bảng `reviews`): Đánh giá 1-5 sao kèm nhận xét sau khi đơn hàng hoàn tất.
- Entity `Complaint` (bảng `complaints`): Tiếp nhận khiếu nại (chưa nhận được hàng, hàng không đúng mô tả), cho phép đính kèm bằng chứng ảnh.
- Admin xem xét đối chất, phán quyết giải quyết khiếu nại và tự động hoàn tiền cho người mua qua ví nếu có vi phạm.

### Ngày 25 – Bảng quản trị tổng hợp Admin Dashboard (`admin`)
- Màn hình Admin Dashboard: [admin/dashboard.html](file:///d:/PTPMHĐT/PROJECT_1/backend/src/main/resources/templates/admin/dashboard.html).
- Thống kê thời gian thực: Tổng số user, tổng phiên đấu giá, tổng đơn hàng, doanh thu phí sàn tích lũy.
- Quản trị người dùng & Xử lý vi phạm: Tra cứu danh sách Unpaid Item Strike, nút khóa tài khoản (Ban) / mở khóa (Unban).
- Tra cứu 50 nhật ký kiểm toán gần nhất từ `audit_logs`.

### Ngày 26 – Dữ liệu demo phong phú, Rà soát toàn diện & Nghiệm thu
- `DatabaseSeeder`: Tự động khởi tạo tài khoản quản trị `admin / admin123`, các tài khoản người bán `seller1`, `seller2` và người mua `buyer1`, `buyer2`, `buyer3` kèm số dư ví mẫu.
- Nạp sẵn các món hàng đa dạng danh mục và các phiên đấu giá đủ trạng thái (`ACTIVE`, `ENDED_SOLD`, có bid tự động proxy).
- Toàn bộ hệ thống hoàn chỉnh, trơn tru, sẵn sàng nghiệm thu và báo cáo đồ án.

---
**Tổng kết kiểm thử tự động toàn diện:** Toàn bộ **41/41 unit tests** của toàn hệ thống từ Task 5 đến Task 26 đều đạt **100% BUILD SUCCESS** (0 failures, 0 errors).
