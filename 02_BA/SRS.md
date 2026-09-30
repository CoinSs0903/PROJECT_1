# 📄 TÀI LIỆU ĐẶC TẢ YÊU CẦU PHẦN MỀM (SRS TEMPLATE)
*Chuẩn IEEE 830 / ISO-IEC-IEEE 29148*

## 1. GIỚI THIỆU TỔNG QUAN (INTRODUCTION)
- **1.1. Mục đích tài liệu:** Mô tả phạm vi, yêu cầu nghiệp vụ và kỹ thuật của hệ thống phần mềm.
- **1.2. Phạm vi dự án:** Mô tả ranh giới hệ thống, những gì hệ thống sẽ giải quyết và những gì nằm ngoài phạm vi (Out of Scope).
- **1.3. Định nghĩa và từ viết tắt:**
  - BA: Business Analyst
  - PM: Project Manager
  - RBAC: Role-Based Access Control
  - CRUD: Create, Read, Update, Delete

## 2. MÔ TẢ TỔNG QUAN HỆ THỐNG (OVERALL DESCRIPTION)
- **2.1. Bối cảnh hoạt động (Product Perspective):** Hệ thống mới hoàn toàn hay nâng cấp, tích hợp cùng hệ thống nào?
- **2.2. Sơ đồ ngữ cảnh hệ thống (Context Diagram):** Luồng dữ liệu giữa Hệ thống và các Tác nhân bên ngoài (Người dùng, Ngân hàng, Cơ quan thuế...).
- **2.3. Các lớp người dùng & Tác nhân (User Classes & Actors):**
  - Quản trị viên (Administrator): Toàn quyền cấu hình hệ thống, phân quyền người dùng.
  - Quản lý / Ban giám đốc: Xem báo cáo thống kê, phê duyệt các yêu cầu lớn.
  - Nhân viên nghiệp vụ: Thực hiện các nghiệp vụ hàng ngày (Lập phiếu, xử lý đơn vị, nhập dữ liệu).
  - Khách hàng / Đối tác bên ngoài: Đăng ký dịch vụ, tra cứu thông tin, gửi yêu cầu.

## 3. BIỂU ĐỒ PHÂN RÃ CHỨC NĂNG (BFD - FUNCTIONAL DECOMPOSITION)
- **Phân hệ 1:** Quản lý danh mục & Thông tin người dùng
  - 1.1. Quản lý thông tin tài khoản & phân quyền
  - 1.2. Quản lý danh mục dùng chung
- **Phân hệ 2:** Quản lý nghiệp vụ cốt lõi (Core Business Operations)
  - 2.1. Tiếp nhận và xử lý hồ sơ / đơn hàng
  - 2.2. Kiểm tra điều kiện hợp lệ & tính khả thi
  - 2.3. Tạo và duyệt phiếu nghiệp vụ / hợp đồng
- **Phân hệ 3:** Quản lý thanh toán & tài chính
  - 3.1. Lập hóa đơn / phiếu thu / phiếu chi
  - 3.2. Đối soát giao dịch & tích hợp cổng thanh toán
- **Phân hệ 4:** Xử lý sự cố & Phát sinh
  - 4.1. Tiếp nhận phản ánh / biên bản sự cố
  - 4.2. Xử lý bồi thường / gia hạn / hủy giao dịch
- **Phân hệ 5:** Báo cáo & Thống kê
  - 5.1. Báo cáo doanh thu / nghiệp vụ định kỳ (ngày, tuần, tháng, năm)
  - 5.2. Báo cáo tổng hợp số liệu cho Ban lãnh đạo

## 4. CHI TIẾT CÁC YÊU CẦU CHỨC NĂNG (FUNCTIONAL REQUIREMENTS - FR)
Mỗi chức năng lá được định nghĩa với bảng tiêu chuẩn sau:

| Thuộc tính | Chi tiết mô tả |
|---|---|
| **Mã chức năng** | FR-CORE-01 |
| **Tên chức năng** | Tiếp nhận và lập phiếu giao dịch |
| **Tác nhân thực hiện** | Nhân viên nghiệp vụ / Lễ tân |
| **Điều kiện bắt đầu** | Khách hàng cung cấp đầy đủ giấy tờ hợp lệ và có nhu cầu |
| **Dữ liệu đầu vào** | Thông tin cá nhân, mã hồ sơ, loại dịch vụ yêu cầu |
| **Quy tắc nghiệp vụ** | - Kiểm tra tính hợp lệ của giấy tờ.<br>- Không cho phép thực hiện nếu khách hàng đang nợ quá hạn hoặc bị khóa tài khoản.<br>- Sinh mã phiếu tự động theo định dạng `GD-YYYYMMDD-XXXX`. |
| **Luồng sự kiện chính** | 1. Nhân viên nhập thông tin đối tượng.<br>2. Hệ thống kiểm tra điều kiện.<br>3. Nhân viên chọn dịch vụ và nhấn "Tạo phiếu".<br>4. Hệ thống lưu cơ sở dữ liệu và in phiếu xác nhận. |
| **Luồng ngoại lệ** | - Nếu không đủ điều kiện: Hệ thống báo lỗi và từ chối tạo phiếu.<br>- Nếu mất kết nối DB: Báo lỗi và ghi log giao dịch thất bại. |
| **Dữ liệu đầu ra** | Phiếu giao dịch hợp lệ, trạng thái lưu vào hệ thống |

## 5. CÁC YÊU CẦU PHI CHỨC NĂNG (NON-FUNCTIONAL REQUIREMENTS - NFR)
- **NFR-01 (Hiệu năng):** Thời gian phản hồi trung bình của các truy vấn API thông thường không quá 1.5 giây với 1000 người dùng đồng thời.
- **NFR-02 (Bảo mật):** Toàn bộ mật khẩu phải được băm bằng thuật toán an toàn (Bcrypt/Argon2); thông tin thanh toán và thẻ phải tuân thủ chuẩn mã hóa SSL/TLS 1.3 và PCI-DSS.
- **NFR-03 (Khả dụng - Availability):** Tỷ lệ hoạt động liên tục (Uptime) đạt tối thiểu 99.5%, có cơ chế tự động chuyển vùng dự phòng (Failover).
- **NFR-04 (Sao lưu & Phục hồi):** Cơ sở dữ liệu được sao lưu tự động hàng ngày lúc 01:00 AM; thời gian phục hồi (RTO) < 2 giờ, mất mát dữ liệu tối đa (RPO) < 1 giờ.

## 6. TỪ ĐIỂN DỮ LIỆU (DATA DICTIONARY)

| STT | Tên trường (Field Name) | Ý nghĩa nghiệp vụ | Kiểu dữ liệu | Độ dài | Ràng buộc / Khuôn dạng |
|---|---|---|---|:---:|---|
| 1 | `user_id` | Mã định danh người dùng | VARCHAR | 20 | Khóa chính, không rỗng, tự sinh |
| 2 | `full_name` | Họ và tên | NVARCHAR | 100 | Không rỗng, viết hoa chữ cái đầu |
| 3 | `identity_card` | Số CCCD / CMND | VARCHAR | 12 | 12 chữ số, duy nhất |
| 4 | `phone_number` | Số điện thoại liên hệ | VARCHAR | 15 | Định dạng chuẩn số điện thoại VN |
| 5 | `status` | Trạng thái bản ghi | VARCHAR | 20 | Giá trị: ACTIVE, PENDING, BLOCKED |
| 6 | `created_at` | Thời điểm tạo | TIMESTAMP | 8 | Tự động gán thời gian hiện tại |
