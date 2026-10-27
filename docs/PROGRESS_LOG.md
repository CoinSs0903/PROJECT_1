# PROGRESS LOG – Nhật Ký Tiến Độ Dự Án

## 1. Trạng Thái Module

| Module | Phụ Trách | Trạng Thái | Ngày Kế Hoạch | Ghi Chú |
| :--- | :--- | :--- | :--- | :--- |
| **môi trường** | Cài đặt JDK, XAMPP, IDE, cấu hình dự án | **Hoàn thành** | Ngày 0 | Đã sẵn sàng JDK 17, MySQL, Spring Boot, docs |
| **docs / db** | SRS, ERD, V1 migration, DECISIONS | **Hoàn thành** | Ngày 1 | ERD 19 bảng, V1 migration chạy sạch |
| **docs** | STATE_MACHINES, BUSINESS_RULES | **Hoàn thành** | Ngày 2 | 6 state machines Mermaid, ma trận 35 BR, 7 scheduler tasks |
| **docs** | SEQUENCES (UC-01 .. UC-04) | **Hoàn thành** | Ngày 3 | 5 sequence diagrams, ranh giới transaction, 2 ví dụ số BR-18 |
| **docs** | SEQUENCES (UC-05 .. UC-08), USE_CASES, ARCHITECTURE, API_MAP | Chưa bắt đầu | Ngày 4 | |
| **common** | Giờ server, tiền VND, Exception, layout nền | Chưa bắt đầu | Ngày 5 | |
| **user** | Tài khoản, đăng nhập, phân quyền, hồ sơ cá nhân | Chưa bắt đầu | Ngày 6, 25 | |
| **wallet** | Ví tiền, giao dịch bất biến, phong tỏa, hoàn, chuyển, đối soát | Chưa bắt đầu | Ngày 7 | |
| **system** | Cấu hình hệ thống, lịch ngày làm việc | Chưa bắt đầu | Ngày 8 | |
| **audit** | Nhật ký chỉ ghi thêm (append-only) | Chưa bắt đầu | Ngày 8, 25 | |
| **notification** | Thông báo trong ứng dụng | Chưa bắt đầu | Ngày 9 | |
| **seller** | Hồ sơ Seller và duyệt hồ sơ | Chưa bắt đầu | Ngày 10 | |
| **auction** | Đăng tài sản, tạo phiên, duyệt phiên, niêm yết, hủy phiên | Chưa bắt đầu | Ngày 11, 12, 14, 15 | |
| **registration** | Đăng ký tham gia, đặt trước, duyệt hồ sơ, mã ẩn danh | Chưa bắt đầu | Ngày 13, 14, 15 | |
| **bidding** | Trả giá tăng dần, chấp nhận giá giảm dần, rút lại giá | Chưa bắt đầu | Ngày 16, 17, 18 | |
| **settlement** | Công bố người trúng, thanh toán, chuyển người kế tiếp | Chưa bắt đầu | Ngày 18, 19, 20 | |
| **realtime** | WebSocket STOMP, đồng bộ giờ, phòng đấu giá | Chưa bắt đầu | Ngày 21, 22 | |
| **payout** | Hoàn tiền, chuyển cọc, nhận tài sản, phí dịch vụ | Chưa bắt đầu | Ngày 23 | |
| **complaint** | Tiếp nhận và xử lý khiếu nại | Chưa bắt đầu | Ngày 24 | |
| **admin** | Quản trị tổng hợp, thống kê, điều hành phiên | Chưa bắt đầu | Ngày 23, 24, 25 | |
| **toàn hệ thống** | Rà soát bảo mật, demo data, nghiệm thu | Chưa bắt đầu | Ngày 26 | |

---

## 2. Nợ Kỹ Thuật (Technical Debt)

| Mã | Nội Dung Làm Tạm | Module | Ngày Gốc | Ngày Dự Kiến Xử Lý | Trạng Thái |
| :--- | :--- | :--- | :--- | :--- | :--- |
| — | Chưa có | — | — | — | — |

---

## 3. Nhật Ký Chi Tiết Theo Ngày

### Ngày 0 – Cài đặt môi trường & Khởi tạo dự án

- **Ngày thực hiện:** 24/10/2026
- **Kết quả đạt được:**
  - Kiểm tra và chuẩn bị JDK 17 (Temurin OpenJDK 17.0.20.1).
  - Cài đặt XAMPP 8.2 và khởi chạy dịch vụ MySQL trên cổng 3306.
  - Tạo 2 database: `auction_db` và `auction_test`; cấu hình tài khoản `auction` / `auction` với đầy đủ quyền hạn.
  - Cài đặt bộ extension Spring Boot (`vmware.vscode-boot-dev-pack`) và Database Client cho Antigravity IDE.
  - Cài đặt Pandoc và chuyển đổi tài liệu đặc tả sang `docs/SRS.md` nguyên vẹn 100% nội dung đặc tả.
  - Khởi tạo khung dự án Spring Boot (Maven Wrapper, Spring Web, Thymeleaf, Spring Data JPA, Flyway, Spring Security, Validation, WebSocket, MySQL Connector).
  - Cấu hình `application.properties` (kết nối `auction_db`) và `application-test.properties` (kết nối `auction_test`).
  - Tạo tài liệu `PROJECT_CONTEXT.md` và `docs/PROGRESS_LOG.md`.
- **Ghi chú:** Đã hoàn thành toàn bộ môi trường và khởi tạo cây thư mục chuẩn.

### Ngày 1 – Tài liệu nền và ERD

- **Ngày thực hiện:** 24/10/2026
- **Kết quả đạt được:**
  - Soạn thảo tài liệu [docs/ERD.md](./ERD.md): Thiết kế 14 thực thể theo SRS Mục 7 và 5 bảng ngoài SRS phục vụ vận hành thực tế (tổng cộng 19 bảng).
  - Soạn thảo tài liệu [docs/DECISIONS.md](./DECISIONS.md): Ghi nhận 13 quyết định nghiệp vụ và các quyết định kỹ thuật (tiền tệ VND, Server Clock, ví hệ thống, tính bất biến).
  - Viết file migration [backend/src/main/resources/db/migration/V1__init.sql](../backend/src/main/resources/db/migration/V1__init.sql) khớp 100% từng cột, kiểu dữ liệu và ràng buộc CHECK với ERD.
  - Flyway áp dụng migration V1 sạch sẽ vào MySQL `auction_db` (19 bảng khởi tạo thành công).
  - Kiểm thử Spring Boot tự động xác nhận migration đạt **BUILD SUCCESS**.
- **Ghi chú:** Sẵn sàng bước vào **Ngày 2 – Sơ đồ trạng thái và ma trận quy tắc nghiệp vụ (STATE_MACHINES & BUSINESS_RULES)**.

### Ngày 2 – Sơ đồ trạng thái và ma trận quy tắc nghiệp vụ

- **Ngày thực hiện:** 27/10/2026
- **Kết quả đạt được:**
  - Soạn thảo tài liệu [docs/STATE_MACHINES.md](./STATE_MACHINES.md): Thiết kế 6 máy trạng thái bằng Mermaid `stateDiagram-v2` (`auctions`, `deposits`, `settlements`, `registrations`, `seller_profiles`, `complaints`), chuẩn hóa danh mục enum tiếng Anh và sự kiện/tác nhân kích hoạt.
  - Soạn thảo tài liệu [docs/BUSINESS_RULES.md](./BUSINESS_RULES.md): Lập ma trận ánh xạ 35 quy tắc nghiệp vụ BR-01..BR-35 sang các module và ngày thực hiện, thiết kế bảng 7 tác vụ tự động ngầm của Scheduler (đóng đăng ký, mở phiên, giảm giá, hết giờ, hạn thanh toán, hoàn tiền, chuyển cọc).
  - Bổ sung tài liệu [docs/DECISIONS.md](./DECISIONS.md): Ghi nhận 4 điểm chuyển trạng thái SRS chưa nêu rõ (quy tắc hủy, xử lý lùi giá khi rút giá BR-17, giới hạn 1 lượt người liền kề BR-18, trạng thái đăng ký khi hủy phiên).
- **Ghi chú:** Đã hoàn thành 100% mục tiêu Ngày 2. Sẵn sàng cho **Ngày 3 – Sequence diagram: đăng ký, trả giá, chấp nhận giá, thanh toán (UC-01 .. UC-04)**.

### Ngày 3 – Sequence diagram: đăng ký, trả giá, chấp nhận giá, thanh toán

- **Ngày thực hiện:** 27/10/2026
- **Kết quả đạt được:**
  - Soạn thảo tài liệu [docs/SEQUENCES.md](./SEQUENCES.md) (Phần 1): Thiết kế 5 sơ đồ tuần tự Mermaid `sequenceDiagram` chi tiết cho 4 ca sử dụng trọng tâm (UC-01 Đăng ký & phong tỏa cọc + xét duyệt Admin, UC-02 Trả giá tăng dần + Rút giá người dẫn đầu BR-17, UC-03 Chấp nhận giá giảm dần tức thì, UC-04 Thanh toán kết quả 15 phút + Quá hạn phạt cọc và xét người liền kề BR-18 / tiếp tục giảm dần BR-22).
  - Xác định ranh giới giao dịch cơ sở dữ liệu (`@Transactional`) và điểm khóa dòng Concurrency (`SELECT ... FOR UPDATE` Pessimistic Lock theo NFR-03).
  - Chốt chi tiết 4 giao dịch nhạy cảm nhất (`placeBid`, `acceptPrice`, `determineWinner`, `settlePayment`).
  - Xây dựng 2 ví dụ số học chuẩn xác cho quy tắc người liền kề theo Điều 51 khoản 1 Luật Đấu giá tài sản (BR-18): 1 ca hợp lệ và 1 ca không hợp lệ làm dữ liệu kiểm thử.
- **Ghi chú:** Đã hoàn thành 100% mục tiêu Ngày 3. Sẵn sàng cho **Ngày 4 – Sequence còn lại (UC-05..08), Use Cases, Kiến trúc Module Facade và Bản đồ API**.
