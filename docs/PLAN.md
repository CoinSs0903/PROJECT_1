# PLAN — Kế hoạch phát triển Hệ thống Đấu giá Trực tuyến (Cơ chế Đấu giá Tự động)

Dự án: Hệ thống Đấu giá Trực tuyến (Java 17, Spring Boot, Thymeleaf + Bootstrap, MySQL qua XAMPP, Flyway).
Định hướng: **Chỉ tập trung vào cơ chế Đấu giá cốt lõi** theo phương thức đấu giá tăng dần tự động (Listing sản phẩm, Đấu giá tự động Proxy Bidding, Giá sàn ẩn Reserve Price, Mua ngay Buy It Now, Đăng lại Relist, Chốt đơn hàng Order).

---

## 1. Quy tắc vàng (Core Principles)

1. **Chỉ làm việc của ngày hiện tại.** Không sửa module khác trừ khi bổ sung hàm vào Facade/Interface dùng chung và ghi vào `PROGRESS_LOG.md`.
2. **Giao tiếp qua Facade & Event:** Module chỉ gọi nhau qua Service/Facade. Trạng thái phiên chỉ thay đổi qua `AuctionStateMachine`. Ví tiền chỉ biến động qua `WalletFacade`.
3. **Database Migration chuẩn chỉ:** Từ Ngày 5 trở đi, không sửa file migration đã commit. Mọi thay đổi cấu trúc bảng phải tạo file version mới `V{n}__*.sql` và đồng bộ vào `docs/ERD.md`.
4. **Bảo mật cơ chế đấu giá:** Tuyệt đối giữ bí mật giá trần (`max_amount`) của `proxy_bids` và con số `reserve_price`. Không bao giờ lộ qua API công khai cho bidder khác.
5. **Đồng bộ tài liệu:** Cuối mỗi ngày cập nhật `PROGRESS_LOG.md`, bảng trạng thái module và các tài liệu liên quan trong `docs/`.

---

## 2. Danh sách Module & Phụ thuộc

| Module | Trách nhiệm chính | Phụ thuộc | Ngày thực hiện |
| :--- | :--- | :--- | :--- |
| **common** | Giờ server, tiện ích tiền tệ, xử lý lỗi chung, entity gốc, layout Bootstrap | Không | 5 |
| **user** | Tài khoản, đăng nhập, phân quyền (Buyer/Seller/Admin), điểm uy tín feedback | common | 6, 25 |
| **wallet** | Ví tiền, giao dịch nạp/rút, thanh toán đơn hàng trúng đấu giá, giải ngân ví | common, user | 7 |
| **system** | Cấu hình bước giá (Bid Increment Tiers), tỷ lệ phí sàn, hạn thanh toán | common | 8 |
| **audit** | Nhật ký kiểm toán bất biến (Append-only Audit Log) | common | 8, 25 |
| **notification** | Báo vượt giá (Outbid), phiên sắp kết thúc, báo thắng cuộc / bán thành công | common | 9 |
| **catalog** | Quản lý danh mục hàng hóa (`categories`), món hàng (`items`, `item_images`) | user, notification | 10 |
| **auction** | Quản lý phiên đấu giá, giá khởi điểm, Reserve Price, Buy It Now, bộ lập lịch đóng/mở | catalog, system, audit | 11, 12, 14, 15 |
| **proxy_bidding** | Bộ máy đấu giá tự động (Proxy Bidding Engine), so khớp Max Bid, ghi nhận bước giá | auction, audit, notification | 13, 16, 17, 18 |
| **order** | Chốt người thắng, đơn hàng trúng đấu giá / mua ngay, thanh toán, xử phạt bùng hàng | auction, proxy_bidding, wallet | 18, 19, 20 |
| **realtime** | WebSocket STOMP, đồng bộ đồng hồ đếm ngược, đẩy giá mới theo thời gian thực | proxy_bidding, auction | 21, 22 |
| **payout** | Giải ngân tiền cho Seller sau khi trừ phí hoa hồng sàn | order, wallet | 23 |
| **complaint** | Khiếu nại món hàng không đúng mô tả hoặc không nhận được hàng | order, notification | 24 |
| **admin** | Bảng điều khiển quản trị, giám sát phiên, thống kê doanh số, xử lý tài khoản vi phạm | Đọc qua Facade các module | 24, 25 |

---

## 3. Kế hoạch chi tiết từng ngày (27 Ngày: Ngày 0 – Ngày 26)

### Giai đoạn 0 — Chuẩn bị môi trường

#### Ngày 0 — Cài đặt môi trường
* **Module:** môi trường | **Quy mô:** Vừa | **Phụ thuộc:** Không
* **Việc cần làm:**
  - Cài đặt JDK 17, Git, XAMPP (chạy MySQL), extension Database Client trong IDE.
  - Tạo 2 database: `auction_db` (phát triển) và `auction_test` (kiểm thử) với user `auction`.
  - Khởi tạo dự án Spring Boot với Spring Initializr (Web, Data JPA, MySQL Driver, Flyway, Thymeleaf, WebSocket, Lombok).
  - Thiết lập file `application.properties` kết nối database, cấu hình Flyway.
  - Chạy thử `mvnw spring-boot:run` kiểm tra mở được `http://localhost:8080`.
  - Khởi tạo Git repo, tạo `.gitignore`, thiết lập `PROJECT_CONTEXT.md` và `docs/PROGRESS_LOG.md`.
* **Đầu ra:** Project Spring Boot chạy được, kết nối DB thành công.

---

### Giai đoạn A — Thiết kế toàn hệ thống (Chưa viết code nghiệp vụ)

#### Ngày 1 — Tài liệu nền tảng và ERD
* **Module:** docs, db | **Quy mô:** Nặng | **Phụ thuộc:** Ngày 0
* **Việc cần làm:**
  - Soạn thảo `docs/SRS.md` tinh gọn: chỉ tập trung vào cơ chế đấu giá tự động C2C, lược bỏ hoàn toàn các thủ tục đăng ký trước và nộp tiền cọc.
  - Thiết kế `docs/ERD.md`: sơ đồ Mermaid `erDiagram` gồm 14 bảng dữ liệu:
    - Bỏ: các bảng hành chính (`registrations`, `deposits`, `holidays`, `auction_minutes`, `listing_change_requests`, `seller_profiles`).
    - Đổi: `assets` $\rightarrow$ `items` (thêm `category_id`, `item_condition` gồm `NEW`, `LIKE_NEW`, `USED`).
    - Sửa bảng `auctions`: Bỏ các cột cọc; thêm `reserve_price`, `buy_it_now_price`, `is_reserve_met`, `bid_count`. Bỏ `UNIQUE(item_id)` để hỗ trợ Đăng lại phiên (**Relist**).
    - Thêm: bảng danh mục sản phẩm (`categories`), bảng lưu giá trần tự động (`proxy_bids`), bảng đơn hàng sau khi thắng (`orders`).
  - Viết file migration khởi tạo `V1__init.sql` khớp 100% với `docs/ERD.md`.
  - Cập nhật `docs/DECISIONS.md` ghi nhận các quyết định thiết kế.
* **Đầu ra:** `docs/SRS.md`, `docs/ERD.md`, `V1__init.sql`, `docs/DECISIONS.md`.

#### Ngày 2 — Sơ đồ trạng thái (State Machines) & Ma trận quy tắc nghiệp vụ
* **Module:** docs | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 1
* **Việc cần làm:**
  - Soạn `docs/STATE_MACHINES.md`:
    - Vòng đời phiên đấu giá (`auctions`): `DRAFT` $\rightarrow$ `ACTIVE` (phân nhánh `RESERVE_NOT_MET` vs `RESERVE_MET`) $\rightarrow$ `ENDED_SOLD` / `ENDED_UNSOLD` / `CANCELED`. Nhánh `ENDED_UNSOLD` $\rightarrow$ `ACTIVE` (Relist).
    - Vòng đời đơn hàng (`orders`): `AWAITING_PAYMENT` $\rightarrow$ `PAID` $\rightarrow$ `CANCELLED_UNPAID`.
  - Cập nhật `docs/BUSINESS_RULES.md`: Quy tắc bước giá tự động theo bậc, quy tắc ẩn giá sàn bảo lưu, điều kiện kích hoạt/ẩn nút Mua ngay, luật xử phạt bùng hàng (Unpaid Item Strike).
* **Đầu ra:** `docs/STATE_MACHINES.md`, `docs/BUSINESS_RULES.md`.

#### Ngày 3 — Sequence Diagram: Đăng bài, Đấu giá tự động, Mua ngay
* **Module:** docs | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 2
* **Việc cần làm:**
  - Soạn `docs/SEQUENCES.md` gồm các luồng chính:
    1. **Sequence Đăng bài (Listing):** Seller đăng `items`, cài đặt Starting Price, Reserve Price, Buy It Now Price, thời lượng phiên.
    2. **Sequence Đấu giá tự động (Proxy Bidding):** Bidder gửi mức giá trần `max_amount` $\rightarrow$ Hệ thống tự động so khớp giá trần hiện tại $\rightarrow$ Tự động tính bước giá mới và đẩy giá công khai.
    3. **Sequence Mua đứt (Buy It Now):** Bidder chọn mua ngay $\rightarrow$ Đóng phiên lập tức $\rightarrow$ Tạo `orders`.
    4. **Sequence Chốt phiên tự động:** Scheduler quét phiên hết giờ $\rightarrow$ Kiểm tra Reserve Price $\rightarrow$ Chốt `ENDED_SOLD` hoặc `ENDED_UNSOLD`.
* **Đầu ra:** `docs/SEQUENCES.md` (4 sơ đồ Mermaid sequence).

#### Ngày 4 — Sequence còn lại, Use Case, Kiến trúc Module & Bản đồ API
* **Module:** docs | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 3
* **Việc cần làm:**
  - Hoàn thiện Sequence: Xử lý người thắng bùng hàng (Unpaid Item), Đăng lại phiên (Relist), Chào giá người về nhì (Second Chance Offer).
  - Soạn `docs/USE_CASES.md`: Use case chi tiết cho Guest, Buyer, Seller, Admin.
  - Soạn `docs/MODULE_INTERFACES.md`: Thiết kế Facade và Event DTO giữa các module (ví dụ: `AuctionClosedEvent`, `ProxyBidPlacedEvent`).
  - Soạn `docs/API_MAP.md`: Danh mục đầy đủ REST API và WebSocket endpoints.
  - Tag git: `phase-a`.
* **Đầu ra:** `docs/USE_CASES.md`, `docs/MODULE_INTERFACES.md`, `docs/API_MAP.md`.

---

### Giai đoạn B — Lập trình từng Module

#### Ngày 5 — Khung dự án, thành phần dùng chung, giao diện nền
* **Module:** common | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 4
* **Việc cần làm:**
  - Cấu trúc package chuẩn: `common`, `user`, `wallet`, `catalog`, `auction`, `bidding`, `order`.
  - Định nghĩa Entity gốc `BaseEntity` (`id`, `created_at`, `updated_at`), enum dùng chung.
  - Thiết lập chuẩn phản hồi API `ApiResponse<T>`, cơ chế xử lý ngoại lệ toàn cục `@ControllerAdvice`.
  - Cấu hình layout giao diện Thymeleaf + Bootstrap 5 (Navbar, Footer, Flash messages, Modal).
* **Đầu ra:** Common code hoàn chỉnh, unit test tiện ích tiền tệ và thời gian.

#### Ngày 6 — Tài khoản, đăng nhập, phân quyền & Hồ sơ uy tín
* **Module:** user | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 5
* **Việc cần làm:**
  - Entity `User`: thông tin định danh, email, mật khẩu mã hóa BCrypt, role (`ROLE_USER`, `ROLE_ADMIN`).
  - Spring Security: Form login, bảo vệ endpoint theo role. Mọi `ROLE_USER` đều có thể vừa mua vừa bán.
  - Màn hình Đăng ký, Đăng nhập, Hồ sơ cá nhân (cập nhật địa chỉ nhận hàng).
  - Theo dõi số lần vi phạm bùng hàng (`unpaid_strikes_count`).
* **Đầu ra:** Chức năng xác thực hoàn chỉnh, kiểm thử bảo mật Controller.

#### Ngày 7 — Ví và giao dịch tiền
* **Module:** wallet | **Quy mô:** Nặng | **Phụ thuộc:** Ngày 6
* **Việc cần làm:**
  - Entity `Wallet` (ví người dùng và ví sàn thu phí `SYSTEM_REVENUE`), `Transaction` (nhật ký giao dịch bất biến).
  - Nghiệp vụ: Nạp tiền (`TOPUP`), Rút tiền (`WITHDRAW`), Thanh toán đơn trúng đấu giá (`PAYMENT`), Nhận tiền hàng (`PAYOUT`), Thu phí sàn (`SERVICE_FEE`).
  - Ràng buộc: `CHECK (available_balance >= 0)`, khóa dòng chống Race Condition khi cập nhật số dư.
  - Màn hình quản lý ví cá nhân và lịch sử giao dịch.
* **Đầu ra:** `WalletFacade`, service giao dịch có kiểm thử Concurrency.

#### Ngày 8 — Cấu hình hệ thống & Nhật ký kiểm toán
* **Module:** system, audit | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 7
* **Việc cần làm:**
  - Entity `SystemConfig`: Bảng bước giá tiêu chuẩn theo bậc, tỷ lệ phí hoa hồng sàn (mặc định 5%), thời hạn thanh toán đơn hàng (3 ngày).
  - Entity `AuditLog`: Tự động ghi vết các hành động quan trọng (đặt giá trần, hủy phiên, sửa listing, thanh toán).
  - Facade đọc cấu hình cache trong bộ nhớ.
* **Đầu ra:** `SystemConfigService`, `AuditLogService`.

#### Ngày 9 — Thông báo trong ứng dụng
* **Module:** notification | **Quy mô:** Nhỏ | **Phụ thuộc:** Ngày 8
* **Việc cần làm:**
  - Entity `Notification`: loại thông báo, tiêu đề, nội dung, đường dẫn liên kết, trạng thái đã đọc.
  - Các sự kiện thông báo cốt lõi:
    - `OUTBID`: Báo người dùng khi có người đặt giá trần cao hơn.
    - `AUCTION_ENDING`: Nhắc trước khi phiên kết thúc 15 phút.
    - `AUCTION_WON`: Báo người mua khi thắng phiên.
    - `ITEM_SOLD`: Báo người bán khi phiên thành công.
  - Menu chuông thông báo trên thanh Navbar.
* **Đầu ra:** `NotificationFacade`, UI chuông thông báo.

#### Ngày 10 — Quản lý Danh mục (Categories) & Món hàng (Items)
* **Module:** catalog | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 9
* **Việc cần làm:**
  - Entity `Category` (hỗ trợ phân cấp cha - con), `Item`, `ItemImage`.
  - Quản lý danh mục hàng hóa (Đồ điện tử, Thời trang, Sưu tầm, v.v.).
  - Chức năng thêm/sửa món hàng của Seller: Tên hàng, tình trạng (`NEW`, `LIKE_NEW`, `USED`), mô tả chi tiết, tải nhiều ảnh sản phẩm.
  - Trang danh sách món hàng đang sở hữu của Seller.
* **Đầu ra:** Catalog service, giao diện quản lý món hàng.

#### Ngày 11 — Đăng bán món hàng & Thiết lập thông số đấu giá
* **Module:** auction | **Quy mô:** Nặng | **Phụ thuộc:** Ngày 10
* **Việc cần làm:**
  - Entity `Auction`: liên kết `item_id`, `starting_price`, `step_price`, `reserve_price`, `buy_it_now_price`, `start_time`, `end_time`, `status`.
  - Màn hình tạo phiên đấu giá (Listing Form):
    - Chọn món hàng từ danh sách `items`.
    - Thiết lập giá khởi điểm và thời lượng phiên (3, 5, 7 ngày).
    - Cài đặt tùy chọn: Giá sàn bảo lưu (Reserve Price) và Mua ngay (Buy It Now).
  - Validation: `reserve_price` $\ge$ `starting_price`; `buy_it_now_price` > `reserve_price`.
* **Đầu ra:** Tạo phiên đấu giá thành công ở trạng thái `ACTIVE` (hoặc `SCHEDULED` nếu hẹn giờ).

#### Ngày 12 — Trang chi tiết phiên đấu giá công khai & Tìm kiếm
* **Module:** auction | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 11
* **Việc cần làm:**
  - Trang chủ & Trang tìm kiếm: Tìm kiếm theo từ khóa, lọc theo danh mục, lọc theo khoảng giá, sắp xếp theo phiên sắp hết giờ.
  - Trang chi tiết phiên đấu giá:
    - Slide ảnh sản phẩm, tình trạng, mô tả, thông tin uy tín Seller.
    - Hiển thị giá hiện tại (`current_price`), số lượt bid (`bid_count`), đồng hồ đếm ngược thời gian thực.
    - Hiển thị nhãn giá sàn: *"Chưa đạt giá sàn"* hoặc *"Đã đạt giá sàn"* (không lộ giá tiền cụ thể).
    - Form nhập giá trần (Place Bid) và nút Mua ngay (Buy It Now).
* **Đầu ra:** UI trang chi tiết hoàn chỉnh.

#### Ngày 13 — Bộ máy Đấu giá tự động (Proxy Bidding Engine - Core Service)
* **Module:** proxy_bidding | **Quy mô:** Nặng | **Phụ thuộc:** Ngày 12
* **Việc cần làm:**
  - Entity `ProxyBid`: lưu mức giá trần tối đa bí mật (`max_amount`) của từng user cho phiên đấu giá.
  - Entity `Bid`: lưu lịch sử các bước giá công khai hiển thị.
  - Thuật toán `ProxyBiddingService.processBid(auctionId, bidderId, maxAmount)`:
    - Kiểm tra tính hợp lệ: `maxAmount` $\ge$ Giá bid tối thiểu tiếp theo (`current_price + step_price`).
    - Khóa bi quan dòng `auctions` (`SELECT ... FOR UPDATE`).
    - So khớp giữa `maxAmount` mới và `max_amount` của người đang dẫn đầu hiện tại:
      - Tự động nhảy bước giá thấp nhất đủ để vượt đối thủ.
      - Cập nhật `current_price`, `highest_bidder_id`, `bid_count`.
      - Kiểm tra và cập nhật cờ `is_reserve_met = TRUE` nếu giá đạt ngưỡng `reserve_price`.
      - Ghi nhận bản ghi vào `bids` (`is_auto_bid = TRUE`).
  - Gửi sự kiện `OutbidEvent` thông báo cho người vừa bị mất vị trí dẫn đầu.
* **Đầu ra:** Service xử lý Proxy Bidding hoàn chỉnh, bộ Unit Test bao phủ 100% các tình huống so khớp giá trần.

#### Ngày 14 — Bộ lập lịch vòng đời phiên (Auction Scheduler & Closer)
* **Module:** auction | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 13
* **Việc cần làm:**
  - Spring `@Scheduled` chạy định kỳ mỗi 5–10 giây:
    - Quét các phiên có `status = 'ACTIVE'` và `end_time <= now()`.
    - Gọi `AuctionStateMachine.closeAuction(auctionId)`.
  - Logic đóng phiên:
    - **Trường hợp 1 (Chưa đạt giá sàn hoặc không ai bid):** Chuyển sang `ENDED_UNSOLD`. Gửi thông báo cho Seller phiên không thành.
    - **Trường hợp 2 (Đã đạt giá sàn và có người bid):** Chuyển sang `ENDED_SOLD`, ghi nhận `highest_bidder_id` là người chiến thắng, phát sự kiện `AuctionWonEvent`.
* **Đầu ra:** Scheduler chạy ngầm tin cậy, tự động chốt phiên đúng thời gian thực.

#### Ngày 15 — Nghiệp vụ Mua đứt (Buy It Now) & Quản lý sửa/hủy Listing
* **Module:** auction | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 14
* **Việc cần làm:**
  - Logic xử lý Mua ngay (Buy It Now):
    - Người mua bấm "Buy It Now" $\rightarrow$ Khóa phiên $\rightarrow$ Đóng phiên lập tức sang `ENDED_SOLD` với giá bán bằng `buy_it_now_price`.
    - Phát sinh đơn hàng ngay lập tức.
  - Quy tắc vận hành: Nút "Buy It Now" tự động bị ẩn ngay khi có lượt bid đầu tiên đạt hoặc vượt giá sàn.
  - Chức năng của Seller: Cho phép chỉnh sửa mô tả tin hoặc hủy phiên trước thời hạn chỉ khi phiên chưa có bất kỳ lượt bid nào.
* **Đầu ra:** API và giao diện Mua ngay mượt mà, ràng buộc nghiệp vụ chặt chẽ.

#### Ngày 16 — Giao diện Đặt giá & Xử lý phản hồi Bị vượt giá (Outbid UI)
* **Module:** proxy_bidding | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 13, 15
* **Việc cần làm:**
  - Modal Đặt giá: Tự động tính toán và hiển thị gợi ý mức giá tối thiểu được phép nhập (`current_price + step_price`).
  - Xử lý phản hồi tức thời: Nếu người dùng nhập Max Bid nhưng vẫn thấp hơn Max Bid bí mật của người dẫn đầu $\rightarrow$ hiển thị cảnh báo ngay: *"Mức giá của bạn đã bị vượt ngay lập tức bởi giá trần của người khác!"*.
  - Bảng lịch sử trả giá công khai (che giấu tên user dạng `a***b`, ẩn con số Max Bid, chỉ hiển thị giá công khai và thời gian bid).
* **Đầu ra:** Giao diện đặt giá trực quan, thân thiện, bảo mật danh tính.

#### Ngày 17 — Xử lý Đấu giá giây cuối & Kiểm soát đồng thời (Concurrency Lock)
* **Module:** proxy_bidding | **Quy mô:** Nặng | **Phụ thuộc:** Ngày 16
* **Việc cần làm:**
  - Tối ưu hóa xử lý đồng thời khi hàng loạt bidder cùng đặt giá trong những giây cuối cùng:
    - Áp dụng kỹ thuật khóa bi quan tối ưu thời gian giữ lock trên bảng `auctions`.
    - Đảm bảo tính nhất quán (Idempotency), không bị race condition làm sai lệch thứ tự bước giá.
  - Viết Integration Test đa luồng mô phỏng nhiều user đồng thời gửi Max Bid ở mili-giây cuối để kiểm chứng tính đúng đắn của người thắng cuộc.
* **Đầu ra:** Bộ test kiểm thử tải đồng thời vượt qua 100%, không bị deadlock.

#### Ngày 18 — Rút lại giá (Bid Retraction) & Chốt người chiến thắng
* **Module:** proxy_bidding, order | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 17
* **Việc cần làm:**
  - Xử lý nghiệp vụ Rút lại giá:
    - Người mua chỉ được yêu cầu rút giá nếu nhập nhầm số tiền lớn bất thường (ví dụ: gõ nhầm 10.000.000 thay vì 1.000.000) và phải thực hiện trước khi hết giờ ít nhất 12 tiếng.
    - Khi rút giá: Xóa `proxy_bids`, tính toán lại giá hiện tại từ các bidder còn lại.
  - Sự kiện chốt người thắng (`AuctionClosedEvent`): Tự động tạo bản ghi trong bảng `orders` ở trạng thái `AWAITING_PAYMENT`.
* **Đầu ra:** Nghiệp vụ rút giá minh bạch, tự động khởi tạo đơn hàng.

#### Ngày 19 — Module Đơn hàng (Order Checkout & Payment)
* **Module:** order | **Quy mô:** Nặng | **Phụ thuộc:** Ngày 18
* **Việc cần làm:**
  - Entity `Order`: liên kết `auction_id`, `buyer_id`, `seller_id`, `final_price`, `shipping_fee`, `total_amount`, `shipping_address`, `status`, `payment_deadline`.
  - Màn hình Thanh toán đơn hàng (Checkout) cho người thắng:
    - Xác nhận địa chỉ nhận hàng.
    - Thanh toán qua số dư Ví nội bộ.
  - Khi thanh toán thành công: Trừ tiền ví Buyer $\rightarrow$ Chuyển trạng thái đơn hàng sang `PAID` (tiền được tạm giữ an toàn) $\rightarrow$ Thông báo cho Seller chuẩn bị giao hàng.
* **Đầu ra:** Quy trình thanh toán đơn hàng trúng đấu giá hoàn chỉnh.

#### Ngày 20 — Đăng lại hàng (Relist), Chào giá người về nhì & Xử phạt bùng hàng
* **Module:** order, auction | **Quy mô:** Nặng | **Phụ thuộc:** Ngày 19
* **Việc cần làm:**
  - Xử lý quá hạn thanh toán (`payment_deadline`):
    - Đơn hàng chuyển sang `CANCELLED_UNPAID`.
    - Tài khoản người mua bị cộng 1 điểm vi phạm bùng hàng (**Unpaid Item Strike**). Nếu tích lũy đủ 3 lần $\rightarrow$ khóa quyền tham gia đấu giá.
  - Tính năng cho Seller:
    - **Second Chance Offer:** Gửi đề nghị bán món hàng cho người về nhì với mức giá cao nhất mà người đó từng bid.
    - **Relist:** Nút 1-click tạo phiên đấu giá mới từ chính món hàng đó (`item_id` được tái sử dụng).
* **Đầu ra:** Cơ chế bảo vệ Seller toàn diện, xử lý bùng hàng chuẩn mực.

#### Ngày 21 — WebSocket và Đồng bộ thời gian thực
* **Module:** realtime | **Quy mô:** Nặng | **Phụ thuộc:** Ngày 20
* **Việc cần làm:**
  - Thiết lập WebSocket với Spring STOMP over SockJS.
  - Kênh `/topic/auctions/{id}`: Đẩy thông tin `current_price`, `bid_count`, `highest_bidder` (mã hóa ẩn danh) ngay khi có bước giá mới.
  - Kênh riêng tư `/user/queue/notifications`: Báo Outbid và kết quả trúng tức thì.
  - Đồng bộ đồng hồ server mili-giây, chống sai lệch giờ ở client.
* **Đầu ra:** Giao diện phòng đấu giá nhảy giá theo thời gian thực không cần tải lại trang.

#### Ngày 22 — Hoàn thiện Giao diện phòng đấu giá & Trải nghiệm Live Bidding
* **Module:** realtime, web | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 21
* **Việc cần làm:**
  - Hoàn thiện UI trang chi tiết: Hiệu ứng nhấp nháy xanh/đỏ khi giá thay đổi, âm thanh thông báo.
  - Tối ưu hiển thị responsive trên thiết bị di động.
  - Kiểm thử trải nghiệm mượt mà từ lúc đặt giá $\rightarrow$ realtime cập nhật $\rightarrow$ hết giờ chốt phiên hiển thị banner chúc mừng.
* **Đầu ra:** Giao diện đấu giá trực tiếp chuyên nghiệp.

#### Ngày 23 — Hoàn tất đơn hàng & Giải ngân ví Người bán (Payout & Fee)
* **Module:** payout | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 22
* **Việc cần làm:**
  - Người mua xác nhận "Đã nhận hàng thành công" (hoặc tự động sau 7 ngày giao hàng):
    - Đơn hàng chuyển sang `COMPLETED`.
    - Hệ thống tự động tính phí sàn (ví dụ 5% trên giá chốt) $\rightarrow$ cộng vào ví doanh thu `SYSTEM_REVENUE`.
    - Số tiền còn lại giải ngân trực tiếp vào Ví của Seller (`available_balance`).
  - Ghi nhận đầy đủ dòng tiền vào bảng bất biến `transactions`.
* **Đầu ra:** Quy trình tài chính khép kín, minh bạch, chính xác.

#### Ngày 24 — Đánh giá uy tín (Feedback/Review) & Khiếu nại
* **Module:** complaint, user | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 23
* **Việc cần làm:**
  - Cơ chế đánh giá 2 chiều: Người mua đánh giá Người bán (sao + nhận xét), cập nhật điểm uy tín tổng quan của Seller.
  - Xử lý khiếu nại (Item Not As Described, Item Not Received):
    - Người mua gửi khiếu nại kèm hình ảnh bằng chứng $\rightarrow$ Đóng băng khoản tiền giải ngân.
    - Admin xem xét đối chất giữa 2 bên để đưa ra phán quyết hoàn tiền cho Buyer hoặc giải ngân cho Seller.
* **Đầu ra:** Module đánh giá uy tín và trung tâm giải quyết khiếu nại.

#### Ngày 25 — Quản trị Admin, Thống kê doanh số & Quản lý vi phạm
* **Module:** admin | **Quy mô:** Vừa | **Phụ thuộc:** Ngày 24
* **Việc cần làm:**
  - Dashboard tổng quan cho Admin:
    - Thống kê tổng số phiên đang chạy, số phiên thành công vs không thành công.
    - Biểu đồ doanh thu phí sàn theo ngày/tháng.
  - Danh sách người dùng vi phạm: Khóa/Mở khóa tài khoản bị dính nhiều Unpaid Item Strike.
  - Tra cứu nhật ký kiểm toán hệ thống (`audit_logs`).
* **Đầu ra:** Bảng điều khiển quản trị toàn diện.

---

### Giai đoạn C — Kiểm thử, Tối ưu & Đóng gói (Ngày 26)

#### Ngày 26 — Rà soát bảo mật, Dữ liệu demo & Nghiệm thu
* **Module:** toàn hệ thống | **Quy mô:** Nặng | **Phụ thuộc:** Ngày 25
* **Việc cần làm:**
  - Chạy toàn bộ test suite (`mvnw clean test`), đảm bảo độ phủ kiểm thử các luồng nghiệp vụ cốt lõi đạt trên 80%.
  - Nạp dữ liệu mẫu phong phú (Seed Data): 20 món hàng đa dạng danh mục (Laptop, Điện thoại, Đồng hồ cơ, Máy ảnh), các phiên đấu giá ở đủ các trạng thái (Sắp diễn ra, Đang chạy sôi nổi, Đã kết thúc có người thắng, Không đạt giá sàn).
  - Hoàn thiện tài liệu `README.md` hướng dẫn cài đặt và kịch bản demo từng bước.
  - Gắn tag git release: `v1.0-auction-system`.
* **Đầu ra:** Hệ thống chạy trơn tru, sẵn sàng nghiệm thu và báo cáo đồ án.

---

## 4. Bảng tổng hợp các điểm cần chốt (Design Decisions)

| # | Vấn đề nghiệp vụ | Đề xuất chốt theo cơ chế đấu giá tự động |
| :-: | :--- | :--- |
| **1** | **Bảng bước giá tối thiểu (Bid Increments)** | Quy định theo các bậc giá: <br>• Giá từ 1 đến 100.000 VNĐ: Bước giá 5.000 VNĐ.<br>• Giá từ 100.000 đến 1.000.000 VNĐ: Bước giá 20.000 VNĐ.<br>• Giá từ 1.000.000 đến 10.000.000 VNĐ: Bước giá 100.000 VNĐ.<br>• Trên 10.000.000 VNĐ: Bước giá 500.000 VNĐ. |
| **2** | **Bảo mật Giá sàn (Reserve Price)** | Người mua chỉ thấy cờ hiệu: `Chưa đạt giá sàn` hoặc `Đã đạt giá sàn`. Con số chính xác được ẩn 100% trong API cho tới khi kết thúc phiên. |
| **3** | **Khi nào nút Buy It Now biến mất?** | Nút Buy It Now sẽ biến mất ngay khi có người đầu tiên đặt một lượt bid hợp lệ đạt ngưỡng giá sàn. |
| **4** | **Quy tắc kết thúc phiên (Sniping)** | Sử dụng **Hard-close** (kết thúc đúng chính xác giây `end_time` định trước). Xử lý tranh chấp giây cuối bằng khóa dòng bi quan DB. |
| **5** | **Thời hạn người thắng phải trả tiền** | Người thắng có tối đa **3 ngày (72 giờ)** để thanh toán đơn hàng. Quá thời gian này tính là bùng hàng. |
| **6** | **Xử lý bùng hàng (Non-paying bidder)** | Cộng 1 gậy vi phạm `unpaid_strikes_count`. Đạt 3 gậy tự động cấm tham gia đấu giá trong 30 ngày. Seller được quyền Relist hoặc gửi Second Chance Offer. |
| **7** | **Đăng lại phiên (Relist)** | 1 món hàng (`Item`) có quan hệ 1-N với `Auctions`. Khi Relist, hệ thống tạo bản ghi `Auction` mới trỏ về cùng `item_id`. |
