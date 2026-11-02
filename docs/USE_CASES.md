# 👥 TÀI LIỆU ĐẶC TẢ CÁC CA SỬ DỤNG (USE CASES)

> **Dự án:** Hệ Thống Đấu Giá Trực Tuyến (Cơ chế Đấu giá Tự động)  
> **Căn cứ:** `docs/SRS.md`, `docs/PLAN.md` (Ngày 4)

---

## 1. DANH SÁCH ACTOR (TÁC NHÂN)
1. **Khách vãng lai (Guest):** Người dùng chưa đăng nhập hệ thống.
2. **Người mua (Buyer):** Người dùng đã đăng nhập tham gia tìm kiếm, đặt giá trần, mua ngay và thanh toán.
3. **Người bán (Seller):** Người dùng đăng bán món hàng, cấu hình phiên, theo dõi và giao hàng.
4. **Quản trị viên (Admin):** Quản lý danh mục, cấu hình tham số sàn, xử lý khiếu nại và người dùng vi phạm.
5. **Hệ thống (System Scheduler):** Tác nhân tự động thực hiện các tác vụ theo thời gian thực.

---

## 2. BIỂU ĐỒ TỔNG QUAN USE CASE (MERMAID)

```mermaid
graph TD
    subgraph Khách vãng lai
        G1[Xem danh sách phiên đấu giá]
        G2[Tìm kiếm và lọc sản phẩm]
        G3[Xem chi tiết phiên & Lịch sử trả giá]
        G4[Đăng ký & Đăng nhập]
    end

    subgraph Người mua (Buyer)
        B1[Đặt mức giá trần Max Bid]
        B2[Sử dụng tính năng Mua ngay Buy It Now]
        B3[Quản lý Ví cá nhân: Nạp / Rút]
        B4[Thanh toán đơn hàng trúng đấu giá]
        B5[Xác nhận đã nhận hàng]
        B6[Đánh giá người bán & Gửi khiếu nại]
        B7[Xem danh sách thông báo]
    end

    subgraph Người bán (Seller)
        S1[Quản lý món hàng Item & Album ảnh]
        S2[Tạo phiên đấu giá Listing]
        S3[Hủy phiên chưa có lượt bid]
        S4[Xác nhận giao hàng & Nhập mã vận đơn]
        S5[Đăng lại phiên Relist khi không thành]
        S6[Gửi Second Chance Offer cho người về nhì]
        S7[Nhận tiền bán hàng sau khi giải ngân]
    end

    subgraph Quản trị viên (Admin)
        A1[Quản lý danh mục Categories]
        A2[Cấu hình hệ thống: Phí sàn, bước giá, hạn nộp]
        A3[Giải quyết tranh chấp khiếu nại Complaints]
        A4[Khóa / Mở khóa tài khoản dính gậy bùng hàng]
        A5[Tra cứu Audit Logs hệ thống]
    end
```

---

## 3. CHI TIẾT CÁC USE CASE TRỌNG TÂM

### UC-B01: Đặt mức giá trần tự động (Proxy Bidding)
- **Tác nhân:** Người mua (Buyer).
- **Tiền điều kiện:** Đã đăng nhập, không phải chủ sở hữu món hàng, tài khoản không bị dính án phạt cấm bid (`status = 'ACTIVE'`), phiên đấu giá đang `ACTIVE`.
- **Luồng chính:**
  1. Người mua mở trang chi tiết phiên đấu giá.
  2. Hệ thống hiển thị giá hiện tại, bước giá gợi ý (`current_price + step_price`) và trạng thái giá sàn (`Chưa đạt giá sàn` / `Đã đạt giá sàn`).
  3. Người mua nhập con số giá tối đa sẵn sàng chi trả (`max_amount`) và nhấn "Xác nhận đặt giá".
  4. Hệ thống kiểm tra: `max_amount >= current_price + step_price`.
  5. Hệ thống khóa dòng phiên và chạy thuật toán Proxy Bidding:
     - So khớp với giá trần cao nhất hiện tại.
     - Nhảy giá công khai lên mức tối thiểu cần thiết để dẫn đầu.
     - Lưu bản ghi giá trần bí mật vào `proxy_bids`.
     - Lưu bước giá hiển thị vào `bids`.
  6. Hệ thống gửi thông báo Outbid cho người bị vượt giá và đẩy giá mới qua WebSocket.
  7. Người mua nhận được thông báo trạng thái: "Bạn đang là người dẫn đầu!" hoặc "Mức giá của bạn đã bị vượt bởi giá trần đối thủ!".
- **Hậu điều kiện:** Giá phiên và số lượt bid tăng lên, người mua giữ vị trí dẫn đầu hoặc bị vượt.

### UC-B02: Mua ngay (Buy It Now)
- **Tác nhân:** Người mua (Buyer).
- **Tiền điều kiện:** Phiên có cài đặt `buy_it_now_price`, chưa có lượt bid nào đạt giá sàn, tài khoản Buyer không phải là Seller.
- **Luồng chính:**
  1. Người mua nhấn nút "Mua ngay với giá X đ".
  2. Hệ thống hiển thị hộp thoại xác nhận mua ngay.
  3. Người mua xác nhận đồng ý.
  4. Hệ thống khóa phiên, cập nhật trạng thái phiên sang `ENDED_SOLD` với giá chốt bằng `buy_it_now_price`.
  5. Hệ thống khởi tạo ngay bản ghi `Order` ở trạng thái `AWAITING_PAYMENT` với thời hạn thanh toán 72 giờ.
  6. Hệ thống chuyển hướng người mua sang trang Thanh toán đơn hàng.
- **Hậu điều kiện:** Phiên kết thúc lập tức, đơn hàng được tạo cho người mua.

### UC-S01: Đăng bán món hàng và khởi tạo phiên đấu giá
- **Tác nhân:** Người bán (Seller).
- **Tiền điều kiện:** Đã đăng nhập tài khoản.
- **Luồng chính:**
  1. Người bán nhập thông tin món hàng: Tên, danh mục, tình trạng (`NEW`, `LIKE_NEW`, `USED`), mô tả, tải lên ảnh sản phẩm.
  2. Người bán thiết lập thông số đấu giá:
     - Giá khởi điểm (`starting_price` >= 0).
     - Bước giá (`step_price` > 0).
     - Giá sàn ẩn (`reserve_price` tùy chọn, nếu có phải >= starting_price).
     - Giá mua ngay (`buy_it_now_price` tùy chọn, nếu có phải > reserve_price).
     - Thời lượng phiên: 3 ngày, 5 ngày hoặc 7 ngày.
  3. Người bán bấm "Đăng phiên đấu giá".
  4. Hệ thống lưu `Item`, `ItemImage`, tạo `Auction` ở trạng thái `ACTIVE` (hoặc `SCHEDULED` nếu hẹn giờ).
- **Hậu điều kiện:** Món hàng xuất hiện trên sàn cho mọi người xem và trả giá.

### UC-S02: Đăng lại phiên đấu giá (Relist)
- **Tác nhân:** Người bán (Seller).
- **Tiền điều kiện:** Phiên trước của món hàng kết thúc ở trạng thái `ENDED_UNSOLD` hoặc đơn hàng bị hủy do người thắng bùng hàng (`CANCELLED_UNPAID`).
- **Luồng chính:**
  1. Người bán vào mục "Món hàng của tôi", chọn món hàng chưa bán được.
  2. Bấm nút "Đăng lại phiên (Relist)".
  3. Hệ thống tải lại toàn bộ thông tin món hàng cũ, cho phép Seller điều chỉnh lại giá khởi điểm, giá sàn, giá mua ngay hoặc thời lượng.
  4. Người bán bấm xác nhận.
  5. Hệ thống tạo một bản ghi `Auction` mới gắn với cùng `item_id` cũ.
- **Hậu điều kiện:** Một phiên đấu giá mới bắt đầu mà không cần nhập lại thông tin hay tải lại ảnh.

