# 🌐 FRONTEND CLIENTS (WEB APPLICATION)

> **Môn học:** Phát Triển Phần Mềm Hướng Đối Tượng (OOAD / PTPMHĐT)  
> **Thành phần:** Toàn bộ giao diện tương tác người dùng phía Client (Web Platform duy nhất, không sử dụng Mobile App)

---

## 📂 CẤU TRÚC THƯ MỤC FRONTEND

```text
frontend/
└── web_app/                   # Ứng dụng Web Đa Cổng (HTML5, Vanilla CSS Glassmorphism, Modern JS)
    ├── index.html             # Cổng Khách Hàng (Bidder / User): Xem phiên, tìm kiếm, lọc, đăng ký tham gia
    ├── auction-room.html      # Phòng Đấu Giá Trực Tuyến: Trả giá tăng dần & giảm dần, đồng hồ đếm ngược, WebSocket
    ├── style.css              # Hệ thống giao diện FinTech Gold & Dark Navy cao cấp, Glassmorphism & Animations
    ├── app.js                 # Xử lý logic phía client, kết nối WebSocket STOMP cập nhật giá real-time
    │
    ├── seller/                # Cổng Dành Cho Người Bán (Seller Portal)
    │   ├── index.html         # Đăng ký hồ sơ người bán, đăng bán tài sản, thiết lập thông số phiên (giá, bước giá)
    │   ├── seller.css         # Phong cách giao diện dành cho Seller
    │   └── seller.js          # Xử lý form gửi duyệt tài sản & theo dõi trạng thái phiên
    │
    └── admin/                 # Cổng Dành Cho Quản Trị Viên (Admin Portal)
        ├── index.html         # Bảng điều khiển Admin: Duyệt hồ sơ Seller, duyệt phiên, xét duyệt hồ sơ tham gia, giám sát phiên
        ├── admin.css          # Giao diện quản trị hiện đại
        └── admin.js           # Xử lý các nghiệp vụ duyệt & điều hành phiên
```

---

## 🚀 CÁC PHÂN HỆ NGƯỜI DÙNG CHÍNH

1. **Cổng Khách Hàng & Đấu Giá Viên (User / Bidder Portal):**
   - Xem danh sách phiên đấu giá công khai, tìm kiếm và lọc theo phương thức (Tăng dần / Giảm dần) và loại tài sản.
   - Nộp tiền đặt trước từ ví và nhận Mã ẩn danh (Anonymous Code).
   - Tham gia phòng đấu giá trực tiếp: xem giá nhảy từng giây, đồng hồ đếm ngược, nút trả giá nhanh.
2. **Cổng Người Bán (Seller Portal):**
   - Đăng ký hồ sơ người bán kèm CCCD và tài khoản ngân hàng nhận tiền.
   - Tạo phiên đấu giá, đính kèm hình ảnh và hồ sơ pháp lý tài sản.
   - Theo dõi kết quả đấu giá và xác nhận bàn giao tài sản.
3. **Cổng Quản Trị Hệ Thống (Admin Portal):**
   - Phê duyệt hồ sơ người bán và tài sản niêm yết.
   - Duyệt danh sách người đăng ký tham gia trước ngày mở phiên.
   - Giám sát phòng đấu giá trực tiếp, hủy phiên khi có vi phạm và xử lý khiếu nại.
