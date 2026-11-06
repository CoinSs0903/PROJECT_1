# 🏛️ HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN TỰ ĐỘNG (ONLINE AUCTION SYSTEM)

> **Môn học:** Phát Triển Phần Mềm Hướng Đối Tượng (OOAD / PTPMHĐT)  
> **Tác giả:** CoinSs0903  
> **Ngôn ngữ & Công nghệ:** Java 17, Spring Boot 3, MySQL 8.0 (XAMPP), Flyway Migration, Spring Security, WebSocket (STOMP), Thymeleaf + Bootstrap 5  
> **Kiến trúc:** Modular Monolith (Giao tiếp liên module qua Facade Pattern)  
> **Cơ chế đấu giá:** Tự động trả giá (Proxy Bidding), Giá sàn bảo lưu (Reserve Price), Mua ngay (Buy It Now), Ví bảo chứng (Escrow Wallet)

---

## 🌟 TỔNG QUAN HỆ THỐNG

Hệ thống cung cấp nền tảng sàn thương mại điện tử chuyên biệt về **Đấu giá trực tuyến tự động**, minh bạch, cạnh tranh thời gian thực và an toàn tài chính. Hệ thống loại bỏ các thủ tục hành chính phức tạp, cho phép người dùng tham gia đấu giá tức thì (**Instant Bidding**) với các cơ chế cốt lõi hiện đại:

1. **Proxy Bidding (Tự động nâng giá thông minh):**
   - Người mua cài đặt mức giá trần tối đa bí mật (**Max Bid**).
   - Hệ thống tự động so khớp và nhảy bước giá thay người mua để luôn duy trì vị thế dẫn đầu với chi phí tiết kiệm nhất.
2. **Reserve Price (Giá sàn bảo lưu ẩn):**
   - Bảo vệ quyền lợi người bán: Mức giá kỳ vọng tối thiểu được bảo mật.
   - Phiên chỉ công nhận kết quả bán thành công nếu mức giá cao nhất đạt hoặc vượt giá sàn.
3. **Buy It Now (Mua ngay):**
   - Cho phép người mua sở hữu món hàng ngay lập tức với mức giá cố định mà không cần chờ hết thời gian đếm ngược của phiên.
4. **Ví thanh toán bảo chứng (Escrow Wallet):**
   - Cơ chế thanh toán an toàn tuyệt đối: Tiền hàng được giữ tại ví trung gian bảo chứng cho đến khi người mua nhận hàng thành công mới giải ngân cho người bán.
5. **Vòng đời Đơn hàng (Orders) & Đăng lại (Relist):**
   - Tự động chốt người trúng và tạo đơn hàng khi phiên kết thúc.
   - Hạn thanh toán rõ ràng (48h - 72h); hỗ trợ xử lý bùng hàng (**Unpaid Strike**) và cho phép người bán đăng lại sản phẩm (**Relist**) dễ dàng.
6. **Đánh giá uy tín (Reviews) & Tiếp nhận khiếu nại (Complaints):**
   - Đánh giá chất lượng 2 chiều giữa Người mua và Người bán.
   - Kênh tiếp nhận và giải quyết khiếu nại minh bạch do Quản trị viên xử lý.

---

## 👥 PHÂN HỆ NGƯỜI DÙNG & TÍNH NĂNG

| Vai trò | Phân hệ / Chức năng chính | Cổng truy cập |
| :--- | :--- | :--- |
| **Người mua (Buyer / Bidder)** | • Xem danh sách & chi tiết món hàng theo danh mục<br>• Trả giá trực tiếp (Instant Bid) hoặc đặt giá trần tự động (Proxy Bid)<br>• Mua đứt tức thì qua tính năng Mua ngay (Buy It Now)<br>• Quản lý ví cá nhân: Nạp tiền, Rút tiền, xem biến động số dư<br>• Theo dõi đơn hàng trúng giá, thanh toán và đánh giá người bán | `/auctions`<br>`/items`<br>`/wallet`<br>`/orders` |
| **Người bán (Seller)** | • Đăng bán món hàng mới kèm thông tin mô tả, tình trạng & album ảnh<br>• Thiết lập thông số phiên: Giá khởi điểm, bước giá, giá sàn ẩn, giá mua ngay, thời gian mở/đóng<br>• Theo dõi tiến trình trả giá theo thời gian thực<br>• Quản lý đơn hàng bán ra, xác nhận giao hàng, nhận tiền giải ngân<br>• Đăng lại (Relist) món hàng khi phiên trước chưa đạt giá sàn | `/items/new`<br>`/items`<br>`/auctions/new` |
| **Quản trị viên (Admin)** | • Bảng điều khiển quản trị tổng quan (Dashboard)<br>• Quản lý danh mục sản phẩm (Categories)<br>• Giám sát toàn bộ phiên đấu giá và hủy phiên vi phạm quy chế<br>• Quản lý ví hệ thống doanh thu sàn (SYSTEM_REVENUE) & ví ký quỹ (SYSTEM_ESCROW)<br>• Tiếp nhận và phán quyết giải quyết khiếu nại (Complaints)<br>• Tra cứu nhật ký kiểm toán hệ thống (Audit Logs) | `/admin`<br>`/admin/dashboard` |

---

## 📂 CẤU TRÚC THƯ MỤC DỰ ÁN (PROJECT STRUCTURE)

```text
PROJECT_1/
├── 📁 backend/                       # Dịch vụ máy chủ Spring Boot & Web Portal
│   ├── .mvn/                         # Cấu hình Maven Wrapper
│   ├── src/main/java/com/auction/    # Mã nguồn Java theo kiến trúc Modular Monolith
│   │   ├── auction/                  # Nghiệp vụ phiên đấu giá & Scheduler đóng phiên
│   │   ├── audit/                    # Nhật ký kiểm toán hệ thống (Audit Logs)
│   │   ├── bidding/                  # Thuật toán Proxy Bidding & Đặt giá thời gian thực
│   │   ├── catalog/                  # Danh mục sản phẩm & Món hàng (Items, Categories)
│   │   ├── common/                   # Tiện ích tiền tệ VND, thời gian chuẩn & Exception
│   │   ├── complaint/                # Tiếp nhận & xử lý khiếu nại người dùng
│   │   ├── notification/             # Hệ thống thông báo tự động
│   │   ├── order/                    # Quản lý đơn hàng, thanh toán & khiếu nại
│   │   ├── realtime/                 # WebSocket STOMP đồng bộ bước giá live
│   │   ├── system/                   # Dữ liệu mẫu (DatabaseSeeder) & Cấu hình hệ thống
│   │   ├── user/                     # Xác thực, phân quyền Spring Security & Hồ sơ cá nhân
│   │   └── wallet/                   # Quản lý ví tiền, giao dịch bất biến & ký quỹ Escrow
│   ├── src/main/resources/
│   │   ├── db/migration/             # Bản cập nhật cấu trúc CSDL Flyway (V1, V2)
│   │   ├── templates/                # Giao diện Web Portal (Thymeleaf + Bootstrap 5)
│   │   └── application.properties   # Cấu hình kết nối MySQL, JPA & Thymeleaf
│   ├── pom.xml                       # Quản lý thư viện phụ thuộc Maven
│   └── mvnw / mvnw.cmd               # Script thực thi Maven độc lập
│
├── 📁 docs/                          # Hệ thống tài liệu đặc tả & thiết kế phần mềm
│   ├── SRS.md                        # Đặc tả yêu cầu phần mềm (35 Quy tắc nghiệp vụ BR)
│   ├── ERD.md                        # Sơ đồ thực thể liên kết chuẩn hóa 15 bảng
│   ├── ERD_EXPLANATION_AND_COMPARISON.md # Phân tích chuyên sâu nghiệp vụ & luồng dữ liệu CSDL
│   ├── BUSINESS_RULES.md             # Tổng hợp 35 quy tắc nghiệp vụ cốt lõi
│   ├── API_MAP.md                    # Bản đồ đặc tả RESTful API
│   ├── SEQUENCES.md                  # Sơ đồ tuần tự các luồng tương tác chính
│   ├── STATE_MACHINES.md             # Máy trạng thái Phiên đấu giá & Đơn hàng
│   ├── DECISIONS.md                  # Nhật ký các quyết định kiến trúc quan trọng
│   ├── PLAN.md                       # Kế hoạch phát triển chi tiết
│   ├── PROJECT_CONTEXT.md            # Ngữ cảnh công nghệ & quy tắc lập trình
│   ├── PROGRESS_LOG.md               # Nhật ký tiến độ thực hiện
│   └── README.md                     # Mục lục hướng dẫn tài liệu
│
├── 📄 start-web.bat                  # Script 1-Click khởi động server an toàn và mở trình duyệt
├── 📄 stop-web.bat                   # Script dừng các tiến trình máy chủ nhanh chóng
├── 📄 run_all.bat                    # Script khởi chạy tổng thể hệ thống
├── 📄 .gitignore                     # Cấu hình loại trừ file của Git
└── 📄 README.md                      # Trang chủ giới thiệu dự án
```

---

## 👥 TÀI KHOẢN TRUY CẬP DEMO (SEEDED ACCOUNTS)

Hệ thống đã tích hợp sẵn dữ liệu khởi tạo demo phong phú ([DatabaseSeeder.java](file:///D:/PTPMH%C4%90T/PROJECT_1/backend/src/main/java/com/auction/system/service/DatabaseSeeder.java)) với các tài khoản sau:

| Vai trò | Tên đăng nhập (Username) | Mật khẩu (Password) | Số dư ví ban đầu | Mục đích thử nghiệm |
| :--- | :--- | :--- | :--- | :--- |
| **Quản trị viên (Admin)** | `admin` | `admin123` | 100.000.000 đ | Quản trị toàn sàn, xem dashboard, duyệt khiếu nại |
| **Người bán (Seller 1)** | `seller1` | `123456` | 20.000.000 đ | Đăng bán hàng công nghệ, theo dõi phiên bán |
| **Người bán (Seller 2)** | `seller2` | `123456` | 15.000.000 đ | Đăng bán hàng thời trang, phụ kiện |
| **Người mua (Buyer 1)** | `buyer1` | `123456` | 50.000.000 đ | Thử nghiệm đặt giá tự động (Proxy Bidding) |
| **Người mua (Buyer 2)** | `buyer2` | `123456` | 80.000.000 đ | Thử nghiệm cạnh tranh bước giá và Mua ngay |
| **Người mua (Buyer 3)** | `buyer3` | `123456` | 10.000.000 đ | Trải nghiệm người dùng thông thường |

---

## 🚀 HƯỚNG DẪN KHỞI CHẠY DỰ ÁN

### Yêu cầu tiên quyết:
- Đã cài đặt **Java 17** (hoặc cao hơn).
- Đã bật dịch vụ **MySQL** trên **XAMPP Control Panel** (cổng `3306`). Database `auction_db` sẽ được Flyway tự động tạo bảng và nạp dữ liệu mẫu khi khởi chạy.

---

### Cách 1: Khởi chạy 1-Click bằng Script (Khuyên dùng)
Nhấp đúp chuột vào file:
```cmd
start-web.bat
```
*(hoặc `run_all.bat`)*

Script sẽ tự động:
1. Kiểm tra kết nối dịch vụ MySQL cổng `3306`.
2. Tự động giải phóng cổng `8080` nếu đang bị chiếm dụng.
3. Khởi động máy chủ Spring Boot thông qua Maven Wrapper.
4. Mở trình duyệt truy cập ngay vào hệ thống tại: **<http://localhost:8080>**

---

### Cách 2: Khởi chạy thủ công bằng dòng lệnh

1. Di chuyển vào thư mục `backend`:
   ```powershell
   cd backend
   ```
2. Khởi chạy server:
   ```powershell
   .\mvnw.cmd test -DforkCount=0 -Dtest=TestServerRunner
   # Hoặc: .\mvnw.cmd spring-boot:run
   ```
3. Mở trình duyệt web và truy cập:
   - **Trang chủ / Sàn đấu giá:** <http://localhost:8080>
   - **Cổng Quản trị Admin:** <http://localhost:8080/admin>
   - **Trang Đăng nhập:** <http://localhost:8080/login>

4. Chạy kiểm thử tự động (Unit & Integration Tests):
   ```powershell
   cd backend
   .\mvnw.cmd test
   ```

---

## 📚 TÀI LIỆU KỸ THUẬT CHI TIẾT (`docs/`)

- 📖 [**Đặc tả Yêu Cầu Phần Mềm (SRS.md)**](./docs/SRS.md) — 35 quy tắc nghiệp vụ BR, Use Cases, NFR.
- 🗄️ [**Thiết Kế Cơ Sở Dữ Liệu (ERD.md)**](./docs/ERD.md) — Chi tiết cấu trúc 15 bảng dữ liệu & sơ đồ Mermaid.
- 📘 [**Giải Thích Nghiệp Vụ ERD (ERD_EXPLANATION_AND_COMPARISON.md)**](./docs/ERD_EXPLANATION_AND_COMPARISON.md) — Phân tích chi tiết luồng nghiệp vụ dữ liệu và tài chính.
- ⚙️ [**35 Quy Tắc Nghiệp Vụ (BUSINESS_RULES.md)**](./docs/BUSINESS_RULES.md) — Toàn bộ ràng buộc logic hệ thống.
- 🔄 [**Sơ Đồ Máy Trạng Thái (STATE_MACHINES.md)**](./docs/STATE_MACHINES.md) — Vòng đời của Phiên đấu giá và Đơn hàng.
- 🗺️ [**Kế Hoạch Dự Án (PLAN.md)**](./docs/PLAN.md) — Lộ trình triển khai chi tiết.

---

## 🌐 KHO LƯU TRỮ GITHUB
- **URL Repository:** [https://github.com/CoinSs0903/PROJECT_1](https://github.com/CoinSs0903/PROJECT_1)
- **Tác giả:** CoinSs0903
