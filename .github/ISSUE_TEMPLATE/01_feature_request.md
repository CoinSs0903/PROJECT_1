---
name: "✨ Yêu cầu tính năng (Feature Request / SRS)"
about: "Đề xuất hoặc mô tả chi tiết chức năng nghiệp vụ mới theo chuẩn tài liệu SRS"
title: "[FEAT] [Mã-FR]: Tên chức năng"
labels: ["enhancement", "feature"]
assignees: ""
---

## 📌 1. THÔNG TIN CHỨC NĂNG (SRS METADATA)
- **Mã chức năng (FR Code):** `FR-CORE-XX` / `FR-0X.X`
- **Phân hệ thuộc về (Module):** 
  - [ ] Phân hệ 1: Quản lý danh mục & Thông tin người dùng
  - [ ] Phân hệ 2: Quản lý nghiệp vụ cốt lõi (Core Business)
  - [ ] Phân hệ 3: Quản lý thanh toán & tài chính
  - [ ] Phân hệ 4: Xử lý sự cố & phát sinh
  - [ ] Phân hệ 5: Báo cáo & Thống kê
- **Tác nhân thực hiện (Actors):** Admin / Quản lý / Nhân viên nghiệp vụ / Khách hàng
- **Độ ưu tiên (Priority):** High / Medium / Low

---

## 📝 2. MÔ TẢ YÊU CẦU NGHIỆP VỤ (BUSINESS REQUIREMENTS)
### Điều kiện bắt đầu (Pre-conditions):
- *Ví dụ: Người dùng đã đăng nhập hệ thống và có vai trò hợp lệ...*

### Dữ liệu đầu vào (Inputs):
- *Ví dụ: Thông tin khách hàng, CCCD, danh mục dịch vụ chọn...*

### Quy tắc nghiệp vụ (Business Rules):
1. Quy tắc 1:...
2. Quy tắc 2:...

### Luồng sự kiện chính (Main Flow):
1. Người dùng mở màn hình...
2. Hệ thống kiểm tra dữ liệu...
3. Người dùng nhấn nút...
4. Hệ thống phản hồi và lưu dữ liệu...

### Luồng sự kiện ngoại lệ / Thất bại (Alternative / Exception Flow):
- **Trường hợp lỗi 1:** Nếu dữ liệu không hợp lệ -> Hệ thống thông báo lỗi...
- **Trường hợp lỗi 2:** Nếu mất kết nối -> Báo lỗi và ghi log...

### Dữ liệu đầu ra (Outputs):
- *Ví dụ: Bản ghi được tạo mới trong CSDL, mã phiếu `GD-YYYYMMDD-XXXX`, email thông báo...*

---

## ✅ 3. TIÊU CHÍ HOÀN THÀNH (ACCEPTANCE CRITERIA - DoD)
- [ ] Đã viết Unit Test bao phủ > 75% logic nghiệp vụ.
- [ ] Tuân thủ nguyên tắc SOLID, Clean Architecture và Coding Convention.
- [ ] Đã kiểm thử thành công trên môi trường Development/Staging.
- [ ] Đã cập nhật tài liệu API / SRS nếu có thay đổi.
