# Hệ Thống Đấu Giá Trực Tuyến (Online Auction System)

Dự án môn học: **Phát Triển Phần Mềm Hướng Đối Tượng (PTPMHĐT)**  
Hệ thống đấu giá trực tuyến hỗ trợ hai phương thức: **đấu giá tăng dần (trả giá lên)** và **đấu giá giảm dần (đặt giá xuống)**, tuân thủ Luật Đấu giá tài sản.

---

## 📚 Tài Liệu Dự Án

| Tài liệu | Mô tả |
| :--- | :--- |
| 📖 [**docs/SRS.md**](./docs/SRS.md) | **Đặc tả yêu cầu phần mềm:** Phạm vi, vai trò, 35 quy tắc nghiệp vụ (BR-01..BR-35), use cases, thực thể CSDL và NFR. |
| 🗺️ [**docs/PLAN.md**](./docs/PLAN.md) | **Kế hoạch thực hiện theo ngày:** Lộ trình 26 ngày từ thiết kế, phát triển module đến kiểm thử & bàn giao. |
| 📋 [**PROJECT_CONTEXT.md**](./PROJECT_CONTEXT.md) | **Sổ tay kỹ thuật & Quy ước:** Kiến trúc, chuẩn đặt tên, cấu hình CSDL và quy tắc làm việc giữa lập trình viên và AI. |
| 📝 [**docs/PROGRESS_LOG.md**](./docs/PROGRESS_LOG.md) | **Nhật ký tiến độ:** Trạng thái từng module, theo dõi nợ kỹ thuật (technical debt) và lịch sử từng phiên làm việc. |

---

## 🛠️ Công Nghệ Sử Dụng

- **Ngôn ngữ:** Java 17 (OpenJDK Temurin)
- **Backend Framework:** Spring Boot 4.x (Spring Web MVC, Spring Data JPA, Spring Security, Spring WebSocket STOMP, Validation)
- **Cơ sở dữ liệu:** MySQL 8.x (chạy qua XAMPP, port 3306)
- **Quản lý CSDL:** Flyway Database Migration
- **Frontend:** Thymeleaf, Bootstrap 5 (CDN), Vanilla JavaScript
- **Build tool:** Maven Wrapper (`mvnw`)

---

## 🚀 Hướng Dẫn Cài Đặt & Khởi Chạy

### 1. Yêu cầu môi trường
- Đã cài đặt **JDK 17** và **Git**.
- Đã cài đặt **XAMPP** và khởi động dịch vụ **MySQL** (cổng 3306).

### 2. Thiết lập cơ sở dữ liệu
Khởi tạo cơ sở dữ liệu trong MySQL (hoặc phpMyAdmin):
```sql
CREATE DATABASE auction_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE auction_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'auction'@'localhost' IDENTIFIED BY 'auction';
GRANT ALL PRIVILEGES ON auction_db.* TO 'auction'@'localhost';
GRANT ALL PRIVILEGES ON auction_test.* TO 'auction'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Chạy ứng dụng
Mở terminal tại thư mục dự án và chạy:
```powershell
# Chạy ứng dụng trên Windows
.\mvnw.cmd spring-boot:run

# Hoặc trên Linux/macOS
./mvnw spring-boot:run
```
Truy cập hệ thống tại: <http://localhost:8080>

### 4. Chạy kiểm thử tự động
```powershell
.\mvnw.cmd test
```

---

## 👥 Nhóm Tác Giả & Phân Quyền
- **Repository:** [https://github.com/CoinSs0903/PROJECT_1](https://github.com/CoinSs0903/PROJECT_1)
- **Chủ sở hữu:** CoinSs0903
