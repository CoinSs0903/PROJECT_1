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
├── user/               # Quản lý tài khoản, xác thực, phân quyền Spring Security (ROLE_USER, ROLE_ADMIN)
├── catalog/            # Quản lý danh mục (Category) và Món hàng đăng bán (Item)
├── auction/            # Phiên đấu giá (Auction), quy chế, Scheduler tự động đóng phiên
├── bidding/            # Lõi đấu giá tự động (Proxy Bidding) & Lịch sử bước giá (Bid)
├── order/              # Đơn hàng (Order) sau đấu giá hoặc Mua ngay (Buy It Now)
├── wallet/             # Quản lý ví cá nhân & hệ thống, giao dịch tiền bất biến, ký quỹ Escrow
├── notification/       # Hệ thống thông báo người dùng (Notification)
├── complaint/          # Tiếp nhận và xử lý khiếu nại (Complaint)
├── audit/              # Nhật ký kiểm toán hệ thống (AuditLog)
├── realtime/           # WebSocket STOMP controller & topic đồng bộ bước giá live
└── system/             # Khởi tạo dữ liệu mẫu (DatabaseSeeder) & Cấu hình hệ thống
```
