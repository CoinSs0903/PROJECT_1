# PROJECT CONTEXT – Hệ Thống Đấu Giá Trực Tuyến

## 1. Thông tin chung
- **Dự án:** Hệ thống đấu giá trực tuyến (Online Auction System)
- **Mục tiêu:** Cung cấp nền tảng đấu giá trực tuyến công bằng, cạnh tranh thời gian thực cho hai phương thức: đấu giá tăng dần (trả giá lên) và đấu giá giảm dần (đặt giá xuống), áp dụng cho động sản và bất động sản.
- **Tài liệu đặc tả (SRS):** [docs/SRS.md](file:///d:/PTPMHĐT/PROJECT_1/docs/SRS.md)
- **Kế hoạch triển khai (PLAN):** [docs/PLAN.md](file:///d:/PTPMHĐT/PROJECT_1/docs/PLAN.md)

---

## 2. Công nghệ & Môi trường
- **Ngôn ngữ:** Java 17 (OpenJDK Temurin 17)
- **Framework:** Spring Boot (Spring Web MVC, Spring Data JPA, Spring Security, Spring WebSocket STOMP, Validation)
- **Giao diện:** Thymeleaf + Bootstrap 5 (CDN) + Vanilla JavaScript
- **Cơ sở dữ liệu:** MySQL 8.x (qua XAMPP, port 3306)
  - Database chính: `auction_db`
  - Database kiểm thử: `auction_test`
  - Tài khoản kết nối: `auction` / `auction`
- **Migration:** Flyway (`classpath:db/migration`)
- **Quản lý build:** Maven Wrapper (`./mvnw`)

---

## 3. Lệnh vận hành
- **Khởi chạy ứng dụng:**
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```
  Truy cập: <http://localhost:8080>
- **Chạy kiểm thử:**
  ```powershell
  .\mvnw.cmd test
  ```

---

## 4. Kiến trúc & Quy ước module
- **Mô hình kiến trúc:** Modular Monolith (Mỗi module là một package độc lập, giao tiếp với nhau duy nhất qua **Facade Interface** đặt tại gốc package).
- **Trạng thái & Nghiệp vụ cốt lõi:**
  - Chỉ `AuctionStateMachine` được quyền thay đổi trạng thái phiên đấu giá.
  - Chỉ `WalletFacade` được quyền thay đổi số dư ví (ghi nhận bất biến qua Transaction).
- **Quy ước cơ sở dữ liệu:**
  - Tên bảng: `snake_case` số nhiều (`users`, `auctions`, `wallets`,...).
  - Tiền tệ: `BIGINT` đơn vị Việt Nam Đồng (VND), không âm.
  - Thời gian: `DATETIME(3)` theo múi giờ `Asia/Ho_Chi_Minh`.
  - Trạng thái: `VARCHAR` lưu tên enum tiếng Anh.
  - Từ Ngày 5 trở đi: Không sửa file migration đã commit, mọi thay đổi DB phải dùng file migration `V...` mới.

---

## 5. Quy tắc vàng cho AI trong từng phiên làm việc
1. **Phạm vi nghiêm ngặt:** Chỉ làm việc trong phạm vi của "Ngày" hiện tại. Không tự ý sửa module khác (trừ khi bổ sung hàm vào Facade và ghi nhận vào PROGRESS_LOG).
2. **Giao tiếp qua Facade:** Module chỉ gọi module khác qua Facade.
3. **Quyết định nghiệp vụ:** Không tự ý thay đổi test để cho qua. Điểm SRS chưa rõ phải ghi vào `docs/DECISIONS.md` và hỏi người dùng.
4. **Nợ kỹ thuật:** Những chỗ làm tạm phải ghi rõ vào mục "Nợ kỹ thuật" trong `docs/PROGRESS_LOG.md` kèm ngày sẽ xử lý chính thức.
5. **Đầu mỗi phiên:** AI bắt buộc đọc `PROJECT_CONTEXT.md`, 2 mục gần nhất trong `docs/PROGRESS_LOG.md`, và dòng "Đọc trước" của ngày hôm nay trong `docs/PLAN.md`.
6. **Cuối mỗi phiên:** Cập nhật nhật ký `docs/PROGRESS_LOG.md`, trạng thái module và các tài liệu liên quan.
