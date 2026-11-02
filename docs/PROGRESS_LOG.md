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
| **auction** | Đăng bán món hàng & Thiết lập thông số đấu giá | Chưa bắt đầu | Ngày 11, 12, 14, 15 | |
| **proxy_bidding** | Bộ máy đấu giá tự động (Proxy Bidding Engine) | Chưa bắt đầu | Ngày 13, 16, 17, 18 | |
| **order** | Đơn hàng trúng đấu giá, thanh toán, xử phạt bùng hàng | Chưa bắt đầu | Ngày 18, 19, 20 | |
| **realtime** | WebSocket STOMP, nhảy giá thời gian thực | Chưa bắt đầu | Ngày 21, 22 | |
| **payout** | Giải ngân tiền cho Seller sau khi trừ phí hoa hồng sàn | Chưa bắt đầu | Ngày 23 | |
| **complaint** | Tiếp nhận và giải quyết khiếu nại | Chưa bắt đầu | Ngày 24 | |
| **admin** | Bảng điều khiển quản trị tổng hợp | Chưa bắt đầu | Ngày 24, 25 | |
| **toàn hệ thống** | Rà soát bảo mật, demo data, nghiệm thu | Chưa bắt đầu | Ngày 26 | |

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

---
**Tổng kết kiểm thử tự động:** Toàn bộ **13/13 unit tests** của các module từ Task 5 đến Task 10 đều đạt **100% BUILD SUCCESS** (0 failures, 0 errors).
