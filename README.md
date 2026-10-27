# 🏛️ HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN (ONLINE AUCTION SYSTEM)

> **Môn học:** Phát Triển Phần Mềm Hướng Đối Tượng (OOAD / PTPMHĐT)  
> **Tác giả:** CoinSs0903  
> **Ngôn ngữ & Công nghệ:** Java 17, Spring Boot, MySQL (XAMPP), Flyway, WebSocket (STOMP), HTML5/CSS3/JavaScript (Single Page Application - SPA)  
> **Phạm vi nền tảng:** Web Platform duy nhất (Bao gồm Cổng Khách Hàng, Cổng Người Bán và Cổng Quản Trị Viên)

---

## 🌟 TỔNG QUAN HỆ THỐNG

Hệ thống cung cấp sàn đấu giá trực tuyến minh bạch, cạnh tranh theo thời gian thực tuân thủ **Luật Đấu giá tài sản số 01/2016/QH14** và các luật sửa đổi bổ sung. Hệ thống phục vụ 3 phân hệ người dùng chính:

1. **Khách hàng / Đấu giá viên (User / Bidder):** Tìm kiếm và lọc danh sách tài sản (động sản & bất động sản), đăng ký tham gia, nộp tiền đặt trước từ ví ký quỹ, nhận Mã ẩn danh (Anonymous Code) và tham gia phòng đấu giá thời gian thực.
2. **Người bán (Seller):** Đăng ký hồ sơ người bán, gửi tài sản, thiết lập thông số đấu giá:
   - **Đấu giá tăng dần (trả giá lên):** Giá khởi điểm, bước giá bắt buộc, thời gian mở và kết thúc.
   - **Đấu giá giảm dần (đặt giá xuống):** Giá khởi điểm, bước giảm, khoảng thời gian giữa các lần giảm và giá sàn.
3. **Quản trị viên (Admin):** Phê duyệt hồ sơ người bán, thẩm định và duyệt niêm yết phiên đấu giá, xét duyệt hồ sơ người tham gia, giám sát phòng đấu giá trực tiếp, giải quyết khiếu nại và trích xuất biên bản điện tử.

---

## 📂 CẤU TRÚC THƯ MỤC DỰ ÁN (PROJECT STRUCTURE)

Cấu trúc chuẩn hóa phân tách rõ ràng giữa Dịch vụ máy chủ (Backend), Giao diện Web (Frontend), Tài liệu kỹ thuật (Docs) và Tài nguyên thiết kế (Design):

```text
PROJECT_1/
├── 📁 backend/               # Dịch vụ máy chủ Spring Boot (REST API & WebSocket STOMP)
│   ├── .mvn/                 # Cấu hình Maven Wrapper
│   ├── src/                  # Mã nguồn Java (Domain, Service, Repository, Facade)
│   ├── mvnw / mvnw.cmd       # Công cụ chạy build độc lập
│   ├── pom.xml               # Quản lý thư viện phụ thuộc
│   └── README.md             # Hướng dẫn chi tiết phân hệ Backend
│
├── 📁 design/                # Tài nguyên đồ họa, bảng màu FinTech & Wireframe
│   └── README.md             # Hướng dẫn hệ thống thiết kế (Design System & Color Palette)
│
├── 📁 docs/                  # Toàn bộ tài liệu đặc tả, kế hoạch & quy ước
│   ├── SRS.md                # Đặc tả yêu cầu phần mềm (35 Quy tắc BR-01..BR-35, FR, UC, NFR)
│   ├── PLAN.md               # Kế hoạch chi tiết 26 ngày thực hiện
│   ├── PROJECT_CONTEXT.md    # Ngữ cảnh kỹ thuật & quy ước AI
│   ├── PROGRESS_LOG.md       # Nhật ký tiến độ & nợ kỹ thuật
│   └── README.md             # Mục lục tài liệu
│
├── 📁 frontend/              # Giao diện người dùng Web Client
│   ├── web_app/              # Ứng dụng Web Đa Cổng
│   │   ├── index.html        # Cổng Khách Hàng / Đấu Giá Viên
│   │   ├── auction-room.html # Phòng Đấu Giá Trực Tuyến Live
│   │   ├── style.css         # Hệ thống giao diện Dark Navy & Luxury Gold
│   │   ├── app.js            # Logic xử lý client & WebSocket
│   │   ├── seller/           # Cổng Dành Cho Người Bán (index.html, seller.js, seller.css)
│   │   └── admin/            # Cổng Dành Cho Quản Trị Viên (index.html, admin.js, admin.css)
│   └── README.md             # Hướng dẫn phân hệ Frontend
│
├── 📁 report/                # Báo cáo tiến độ và tổng kết dự án bằng LaTeX
│   ├── PROJECT_REPORT.tex    # Mã nguồn LaTeX báo cáo tiến độ (Buổi 0 & 1, Kế hoạch tiếp theo)
│   └── PROJECT_REPORT.pdf    # Bản PDF xuất bản chính thức (7 trang)
│
├── 📄 run_all.bat            # Script 1-click khởi chạy toàn bộ hệ thống
├── 📄 .gitignore             # Bộ lọc mã nguồn Git
└── 📄 README.md              # Trang chủ dự án
```

---

## 👥 TÀI KHOẢN TRUY CẬP MẪU

| Vai trò | Tên đăng nhập | Mật khẩu | Chức năng chính | Cổng truy cập |
| :--- | :--- | :--- | :--- | :--- |
| **Đấu giá viên (Bidder)** | `bidder1` | `bidder123` | Đặt trước, trả giá live, theo dõi kết quả | `frontend/web_app/index.html` |
| **Người bán (Seller)** | `seller1` | `seller123` | Đăng tài sản, tạo phiên, nhận tiền cọc | `frontend/web_app/seller/index.html` |
| **Quản trị viên (Admin)** | `admin` | `admin123` | Duyệt hồ sơ, duyệt phiên, điều hành | `frontend/web_app/admin/index.html` |

---

## 🚀 HƯỚNG DẪN KHỞI CHẠY DỰ ÁN

### Cách 1: Khởi chạy 1-Click bằng file Script (Khuyên dùng)
1. Đảm bảo đã bật **MySQL** trên **XAMPP Control Panel** (cổng `3306`).
2. Nhấp đúp chuột vào file:
   ```cmd
   run_all.bat
   ```
   Hệ thống sẽ tự động kiểm tra cổng 3306, mở giao diện Web App trên trình duyệt và khởi chạy Spring Boot Backend trên cổng `8080`.

---

### Cách 2: Khởi chạy thủ công từng phân hệ

#### 1. Khởi chạy Backend:
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```
Backend API & WebSocket sẵn sàng tại: <http://localhost:8080>

#### 2. Khởi chạy Frontend:
Mở trực tiếp file `frontend/web_app/index.html` trên trình duyệt (hoặc dùng tiện ích Live Server trong IDE).

#### 3. Chạy kiểm thử tự động Backend:
```powershell
cd backend
.\mvnw.cmd test
```

---

## 📚 DANH MỤC TÀI LIỆU CHI TIẾT (`docs/`)

- 📖 [**Đặc tả yêu cầu phần mềm (docs/SRS.md)**](./docs/SRS.md)
- 🗺️ [**Kế hoạch thực hiện theo từng ngày (docs/PLAN.md)**](./docs/PLAN.md)
- 📋 [**Sổ tay kỹ thuật & Quy ước AI (docs/PROJECT_CONTEXT.md)**](./docs/PROJECT_CONTEXT.md)
- 📝 [**Nhật ký tiến độ dự án (docs/PROGRESS_LOG.md)**](./docs/PROGRESS_LOG.md)
- 🎨 [**Tài nguyên thiết kế giao diện (design/README.md)**](./design/README.md)

---

## 🌐 KHO LƯU TRỮ GITHUB
- **URL Repository:** [https://github.com/CoinSs0903/PROJECT_1](https://github.com/CoinSs0903/PROJECT_1)
- **Tác giả:** CoinSs0903
