# PROGRESS LOG – Nhật Ký Tiến Độ Dự Án

## 1. Trạng Thái Module

| Module | Phụ Trách | Trạng Thái | Ngày Kế Hoạch | Ghi Chú |
| :--- | :--- | :--- | :--- | :--- |
| **môi trường** | Cài đặt JDK, XAMPP, IDE, cấu hình dự án | **Hoàn thành** | Ngày 0 | Đã sẵn sàng JDK 17, MySQL, Spring Boot, docs |
| **docs / db** | SRS, ERD, V1 migration, DECISIONS | **Hoàn thành** | Ngày 1 | ERD 19 bảng, V1 migration chạy sạch |
| **docs** | STATE_MACHINES, BUSINESS_RULES | Chưa bắt đầu | Ngày 2 | |
| **docs** | SEQUENCES (UC-01 .. UC-04) | Chưa bắt đầu | Ngày 3 | |
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
| **toàn hệ thống**| Rà soát bảo mật, demo data, nghiệm thu | Chưa bắt đầu | Ngày 26 | |

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

