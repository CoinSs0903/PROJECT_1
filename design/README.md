# 🎨 DESIGN SYSTEM & TÀI NGUYÊN THIẾT KẾ (AUCTION DESIGN SYSTEM)

> **Dự án:** Hệ Thống Đấu Giá Trực Tuyến (Online Auction System)  
> **Chủ đề:** Sàn đấu giá trực tuyến cao cấp, bảo mật & thời gian thực (Premium FinTech & Auction House Style)

---

## 🎨 HỆ MÀU SẮC ĐẶC TRƯNG (COLOR PALETTE)

- **Nền chính (Background):**
  - Dark Theme: `#0B0F19` (Xanh đen hải quân huyền bí) kết hợp `#111827` (Slate Dark)
  - Light Theme: `#F8FAFC` (Trắng ngọc trai / Pearl White)
- **Thẻ giao diện (Cards & Containers):** `#1E293B` có viền kính mờ (Glassmorphism viền mạ vàng / viền neon)
- **Màu điểm nhấn thương hiệu (Luxury Accent / Primary):**
  - 🟡 **Vàng Hoàng Gia (Gold Luxury):** `#F59E0B` & `#D97706` (Thể hiện giá trị tài sản, uy tín và sự sang trọng)
  - 🔵 **Xanh Công Nghệ (Royal Blue):** `#2563EB` & `#3B82F6` (Độ tin cậy và minh bạch)
- **Màu trạng thái phiên đấu giá (Auction State Indicators):**
  - 🟢 **Đang diễn ra (Live Bidding):** `#10B981` (Hiệu ứng nhấp nháy Pulse xanh ngọc)
  - 🟡 **Chờ mở phiên / Chờ duyệt:** `#F59E0B`
  - 🔴 **Chờ thanh toán / Đã kết thúc:** `#EF4444`
  - ⚪ **Không thành / Đã hủy:** `#6B7280`

---

## 📐 BỐ CỤC PHÒNG ĐẤU GIÁ THỜI GIAN THỰC (REAL-TIME AUCTION ROOM)

1. **Khu vực hiển thị tài sản (Asset Showcase):** Hình ảnh chất lượng cao, thông tin thẩm định, người bán, loại tài sản (động sản / bất động sản).
2. **Bảng giá & Đồng hồ đếm ngược (Live Price & Server Clock):**
   - Giá hiện tại hiển thị kích thước lớn (Bold VND).
   - Đồng hồ đếm ngược từng giây đồng bộ tuyệt đối với Server Clock (NFR-02).
   - Thanh tiến trình thời gian và mức giá sàn (ở phương thức giảm dần).
3. **Bảng điều khiển trả giá (Bidding Action Console):**
   - Các nút chọn nhanh mức tăng: `+ 1 bước giá`, `+ 2 bước giá`, `+ 5 bước giá`.
   - Nút chấp nhận giá tức thì (ở phương thức đặt giá xuống).
4. **Nhật ký trả giá thời gian thực (Live Bid Stream):**
   - Hiển thị danh sách trả giá cập nhật real-time qua WebSocket STOMP.
   - Bảo mật danh tính: 100% hiển thị bằng **Mã ẩn danh (Anonymous Code)** (ví dụ: `Bidder #829104`).

---

## 📂 TÀI NGUYÊN GIAO DIỆN

- Giao diện Web Client nằm tại: `frontend/web_app/`
- Giao diện tích hợp Template Engine nằm tại: `backend/src/main/resources/templates/`
