# ☕ BACKEND SERVICE (SPRING BOOT & MYSQL)

> **Môn học:** Phát Triển Phần Mềm Hướng Đối Tượng (OOAD / PTPMHĐT)  
> **Thành phần:** Dịch vụ xử lý nghiệp vụ, quản trị dữ liệu và kết nối thời gian thực phía máy chủ

---

## 🛠️ CÔNG NGHỆ SỬ DỤNG

- **Java Version:** OpenJDK Temurin 17
- **Framework:** Spring Boot 4.x
- **ORM / Persistence:** Spring Data JPA (Hibernate)
- **Database:** MySQL 8.x (chạy qua XAMPP, port 3306)
- **Migration:** Flyway Database Migration (`src/main/resources/db/migration/`)
- **Bảo mật:** Spring Security (BCrypt, Session & Cookie)
- **Thời gian thực:** Spring WebSocket (STOMP protocol)
- **Kiểm thử:** JUnit 5, ArchUnit, TestContainers / H2 / MySQL Test

---

## 🚀 HƯỚNG DẪN KHỞI CHẠY BACKEND

### 1. Khởi chạy ứng dụng máy chủ:
```powershell
# Chạy trên Windows
.\mvnw.cmd spring-boot:run

# Hoặc trên Linux/macOS
./mvnw spring-boot:run
```
Máy chủ sẽ khởi động trên cổng: **`http://localhost:8080`**

### 2. Chạy bộ kiểm thử tự động:
```powershell
.\mvnw.cmd test
```

---

## 📂 CẤU TRÚC GÓI (PACKAGE STRUCTURE)

```text
src/main/java/com/auction/
├── common/             # Các lớp dùng chung: ServerClock, Money VND, BaseEntity, Exception
├── user/               # Tài khoản, xác thực, phân quyền (User, Guest, Admin, SellerProfile)
├── wallet/             # Quản lý ví, giao dịch tiền bất biến, ký quỹ, phong tỏa & đối soát
├── auction/            # Tài sản, phiên đấu giá, quy chế, máy trạng thái AuctionStateMachine
├── registration/       # Đăng ký tham gia phiên, nộp tiền đặt trước, mã ẩn danh (Anonymous Code)
├── bidding/            # Lõi đấu giá: Đấu giá tăng dần (placeBid) & giảm dần (acceptPrice)
├── settlement/         # Công bố người trúng, thanh toán sau phiên, biên bản điện tử
├── realtime/           # WebSocket STOMP controller & topic đồng bộ giá thời gian thực
├── payout/             # Hoàn tiền cọc, giải ngân cho Seller, tính phí hệ thống
├── complaint/          # Tiếp nhận và xử lý khiếu nại
└── admin/              # Tổng hợp quản trị và điều hành phiên
```
