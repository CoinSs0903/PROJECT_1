# 🚀 BẢN GHI CHÚ PHÁT HÀNH PHIÊN BẢN (RELEASE NOTES)

**Tên sản phẩm:** Hệ Thống Quản Lý Phần Mềm (PROJECT_1)  
**Phiên bản (Version):** `v1.0.0`  
**Ngày phát hành:** 2026-09-23  
**Người chịu trách nhiệm phát hành:** Project Manager & Tech Lead  
**Môi trường triển khai:** Production (`https://app.project1.com`)

---

## 🌟 1. CÁC TÍNH NĂNG MỚI (NEW FEATURES)
- **[AUTH-01]** Tích hợp đăng nhập an toàn bằng JWT và xác thực phân quyền 2 lớp (2FA).
- **[CORE-02]** Chức năng tiếp nhận hồ sơ, tạo phiếu giao dịch tự động kèm mã vạch / mã QR.
- **[PAY-03]** Tích hợp cổng thanh toán trực tuyến và tự động sinh hóa đơn điện tử.
- **[RPT-04]** Bảng điều khiển phân tích số liệu (Dashboard) dành cho Ban giám đốc, tự động xuất báo cáo định dạng Excel / PDF.

## ⚡ 2. CẢI TIẾN & TỐI ƯU HÓA (IMPROVEMENTS)
- Tối ưu hóa câu truy vấn CSDL báo cáo doanh số, giảm thời gian phản hồi từ 4.2s xuống còn 0.6s.
- Bổ sung bộ đệm In-memory Caching (Redis) cho danh mục sản phẩm và dữ liệu dùng chung.
- Cải thiện trải nghiệm giao diện người dùng (Responsive) mượt mà trên thiết bị di động và máy tính bảng.

## 🐛 3. CÁC LỖI ĐÃ KHẮC PHỤC (BUG FIXES)
- **[BUG-102]** Khắc phục lỗi không tải được ảnh định dạng PNG dung lượng trên 5MB.
- **[BUG-115]** Sửa lỗi tính sai số tiền phạt khi độc giả trả sách / thanh lý tài sản quá hạn 1 tuần.
- **[BUG-128]** Khắc phục hiện tượng hiển thị sai múi giờ (UTC+7) trên biên lai giao dịch.

## ⚠️ 4. LƯU Ý KỸ THUẬT & HƯỚNG DẪN NÂNG CẤP (MIGRATION NOTES)
- Cơ sở dữ liệu: Cần chạy script migration `V1_0_0__release.sql` trước khi triển khai bản dựng mới.
- Biến môi trường: Bổ sung thêm biến `REDIS_CONNECTION_STRING` và `JWT_SECRET_KEY` trong file cấu hình `.env`.
