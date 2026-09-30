# 📋 DANH MỤC GITHUB ISSUES & PRODUCT BACKLOG
*Được đồng bộ trực tiếp từ Tài liệu Đặc tả Yêu cầu Phần mềm ([02_BA/SRS.md](../02_BA/SRS.md))*

Tài liệu này chứa danh sách toàn bộ các Issues đại diện cho các chức năng của hệ thống để khởi tạo trên **GitHub Issues / GitHub Projects (Kanban Board)**.

---

## 🏷️ HỆ THỐNG LABELS TRÊN GITHUB

| Label | Màu sắc | Ý nghĩa |
|---|:---:|---|
| `module:01-user-role` | `#0E8A16` | Phân hệ 1: Quản lý người dùng & phân quyền |
| `module:02-core-ops` | `#1D76DB` | Phân hệ 2: Quản lý nghiệp vụ cốt lõi |
| `module:03-finance` | `#FBCA04` | Phân hệ 3: Thanh toán & tài chính |
| `module:04-incident` | `#D93F0B` | Phân hệ 4: Xử lý sự cố & phát sinh |
| `module:05-reporting`| `#5319E7` | Phân hệ 5: Báo cáo & thống kê |
| `priority:high` | `#B60205` | Ưu tiên cao - Hoàn thành trong Sprint 1 & 2 |
| `priority:medium` | `#E99695` | Ưu tiên trung bình |
| `priority:low` | `#C5DEF5` | Ưu tiên thấp |

---

## 🎯 DANH SÁCH 11 GITHUB ISSUES CHI TIẾT

### 🔹 Phân hệ 1: Quản lý danh mục & Thông tin người dùng

#### **Issue #1: [FR-01.1] Quản lý thông tin tài khoản & phân quyền người dùng (RBAC)**
- **Labels:** `module:01-user-role`, `priority:high`, `enhancement`
- **Milestone:** `Sprint 1 - Foundation & Core Setup`
- **Nhánh phát triển:** `feature/FR-01_user-role-management`
- **Mô tả (User Story):**
  > Là **Quản trị viên (Admin)**, tôi muốn tạo tài khoản, gán quyền truy cập (Admin, Manager, Staff, Customer) và quản lý trạng thái tài khoản (ACTIVE, PENDING, BLOCKED) để bảo mật hệ thống và phân định rõ vai trò làm việc.
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [ ] Mật khẩu được băm an toàn bằng Bcrypt/Argon2 theo NFR-02.
  - [ ] Cơ chế xác thực JWT và phân quyền Middleware Role-Based Access Control (RBAC).
  - [ ] Cho phép Admin khóa/mở khóa tài khoản ngay lập tức.
  - [ ] Unit Test kiểm tra phân quyền đạt độ bao phủ > 80%.

#### **Issue #2: [FR-01.2] Quản lý danh mục dùng chung hệ thống**
- **Labels:** `module:01-user-role`, `priority:medium`, `enhancement`
- **Milestone:** `Sprint 1 - Foundation & Core Setup`
- **Nhánh phát triển:** `feature/FR-01_user-role-management`
- **Mô tả (User Story):**
  > Là **Quản trị viên**, tôi muốn thêm, sửa, xóa, tìm kiếm danh mục dịch vụ, bảng giá và trạng thái dùng chung để các phân hệ nghiệp vụ có thể tham chiếu dữ liệu đồng nhất.
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [ ] Đầy đủ API CRUD cho danh mục dịch vụ.
  - [ ] Áp dụng cơ chế Soft-delete (xóa mềm) để không làm gãy dữ liệu lịch sử.
  - [ ] Có bộ nhớ đệm (Cache Redis) cho các danh mục ít thay đổi để tối ưu hiệu năng theo NFR-01.

---

### 🔹 Phân hệ 2: Quản lý nghiệp vụ cốt lõi (Core Business Operations)

#### **Issue #3: [FR-02.1] Tiếp nhận và xử lý hồ sơ / đơn hàng dịch vụ**
- **Labels:** `module:02-core-ops`, `priority:high`, `enhancement`
- **Milestone:** `Sprint 2 - Core Operations`
- **Nhánh phát triển:** `feature/FR-02_core-business-operations`
- **Mô tả (User Story):**
  > Là **Nhân viên nghiệp vụ / Khách hàng**, tôi muốn tạo mới và theo dõi tiến trình xử lý hồ sơ giao dịch để các bước công việc được diễn ra liền mạch.
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [ ] Tiếp nhận đầy đủ thông tin: họ tên, CCCD (12 số), số điện thoại hợp lệ (theo Từ điển dữ liệu SRS mục 6).
  - [ ] Lưu vết trạng thái hồ sơ: `RECEIVED`, `PROCESSING`, `VERIFIED`, `COMPLETED`.
  - [ ] Gửi thông báo cập nhật tiến độ qua Email / Hệ thống.

#### **Issue #4: [FR-02.2] Kiểm tra điều kiện hợp lệ & tính khả thi hồ sơ**
- **Labels:** `module:02-core-ops`, `priority:high`, `enhancement`
- **Milestone:** `Sprint 2 - Core Operations`
- **Nhánh phát triển:** `feature/FR-02_core-business-operations`
- **Mô tả (User Story):**
  > Là **Hệ thống / Chuyên viên thẩm định**, tôi muốn hệ thống tự động kiểm tra giấy tờ, tình trạng nợ quá hạn và tính hợp lệ trước khi cho phép lập phiếu để giảm thiểu rủi ro vận hành.
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [ ] Kiểm tra số CCCD đã tồn tại và không bị đánh dấu vi phạm.
  - [ ] Từ chối tiếp nhận nếu đối tượng đang có công nợ quá hạn hoặc tài khoản bị khóa.
  - [ ] Trả về thông báo lỗi rõ ràng nếu không đáp ứng điều kiện.

#### **Issue #5: [FR-02.3] Lập và phê duyệt phiếu nghiệp vụ / hợp đồng (FR-CORE-01)**
- **Labels:** `module:02-core-ops`, `priority:high`, `enhancement`
- **Milestone:** `Sprint 2 - Core Operations`
- **Nhánh phát triển:** `feature/FR-02_core-business-operations`
- **Mô tả (User Story):**
  > Là **Nhân viên nghiệp vụ**, tôi muốn sinh mã phiếu tự động định dạng `GD-YYYYMMDD-XXXX` và chuyển cấp Quản lý phê duyệt để hoàn tất thủ tục giao dịch chính thức.
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [ ] Sinh mã phiếu tự động duy nhất theo mẫu: `GD-YYYYMMDD-XXXX`.
  - [ ] Lưu trạng thái phiếu vào CSDL và xuất bản in PDF phiếu xác nhận.
  - [ ] Quản lý có quyền Duyệt (Approve) hoặc Từ chối kèm lý do (Reject with reasons).

---

### 🔹 Phân hệ 3: Quản lý thanh toán & tài chính

#### **Issue #6: [FR-03.1] Lập hóa đơn, phiếu thu & phiếu chi tự động**
- **Labels:** `module:03-finance`, `priority:high`, `enhancement`
- **Milestone:** `Sprint 3 - Finance & Payments`
- **Nhánh phát triển:** `feature/FR-03_payment-finance`
- **Mô tả (User Story):**
  > Là **Nhân viên thu ngân / Kế toán**, tôi muốn lập phiếu thu/chi và xuất hóa đơn điện tử cho từng phiếu giao dịch với tính toán thuế chính xác.
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [ ] Tự động tính tiền hàng, thuế VAT và các khoản giảm giá.
  - [ ] Đảm bảo tính toàn vẹn dữ liệu tài chính (ACID transaction).
  - [ ] Hỗ trợ xuất mẫu hóa đơn chuẩn PDF để gửi khách hàng.

#### **Issue #7: [FR-03.2] Tích hợp cổng thanh toán trực tuyến & Đối soát giao dịch**
- **Labels:** `module:03-finance`, `priority:medium`, `enhancement`
- **Milestone:** `Sprint 3 - Finance & Payments`
- **Nhánh phát triển:** `feature/FR-03_payment-finance`
- **Mô tả (User Story):**
  > Là **Khách hàng & Kế toán**, tôi muốn thanh toán qua cổng VNPAY/Momo/Chuyển khoản QR và đối soát tự động hàng ngày để đảm bảo minh bạch dòng tiền.
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [ ] Tích hợp Webhook nhận kết quả thanh toán tức thời từ Cổng thanh toán.
  - [ ] Kiểm tra chữ ký số (HMAC SHA512) để chống giả mạo giao dịch.
  - [ ] Module tự động đối soát chênh lệch cuối ngày và gửi cảnh báo nếu lệch sổ.

---

### 🔹 Phân hệ 4: Xử lý sự cố & Phát sinh

#### **Issue #8: [FR-04.1] Tiếp nhận phản ánh, khiếu nại & lập biên bản sự cố**
- **Labels:** `module:04-incident`, `priority:medium`, `enhancement`
- **Milestone:** `Sprint 4 - Incident & Support`
- **Nhánh phát triển:** `feature/FR-04_incident-handling`
- **Mô tả (User Story):**
  > Là **Khách hàng / Nhân viên CSKH**, tôi muốn gửi yêu cầu hỗ trợ và lập biên bản ghi nhận khi có sự cố phát sinh trong quá trình sử dụng dịch vụ.
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [ ] Cung cấp form gửi khiếu nại kèm ảnh bằng chứng sự cố.
  - [ ] Sinh mã sự cố `SC-YYYYMMDD-XXXX` và phân loại mức độ nghiêm trọng (Blocker/High/Normal/Low).
  - [ ] Đặt SLA phản hồi tự động theo quy định của công ty.

#### **Issue #9: [FR-04.2] Quy trình thẩm định bồi thường, gia hạn hoặc hủy giao dịch**
- **Labels:** `module:04-incident`, `priority:medium`, `enhancement`
- **Milestone:** `Sprint 4 - Incident & Support`
- **Nhánh phát triển:** `feature/FR-04_incident-handling`
- **Mô tả (User Story):**
  > Là **Quản lý phê duyệt**, tôi muốn xem xét biên bản sự cố để duyệt phương án bồi thường, gia hạn thời gian thực hiện hoặc hoàn tiền/hủy giao dịch.
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [ ] Luồng phê duyệt đa cấp tùy theo giá trị bồi thường.
  - [ ] Tự động đồng bộ sang Phân hệ 3 (Tài chính) nếu có quyết định hoàn tiền (Refund).
  - [ ] Ghi lại đầy đủ lịch sử Audit Log người duyệt và thời điểm duyệt.

---

### 🔹 Phân hệ 5: Báo cáo & Thống kê

#### **Issue #10: [FR-05.1] Báo cáo doanh thu & số lượng nghiệp vụ định kỳ**
- **Labels:** `module:05-reporting`, `priority:medium`, `enhancement`
- **Milestone:** `Sprint 5 - Analytics & Release`
- **Nhánh phát triển:** `feature/FR-05_reporting-analytics`
- **Mô tả (User Story):**
  > Là **Trưởng phòng nghiệp vụ**, tôi muốn xem và xuất báo cáo số lượng giao dịch, doanh số theo ngày, tuần, tháng, quý dưới định dạng Excel và PDF.
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [ ] Bộ lọc thời gian linh hoạt (Từ ngày -> Đến ngày), lọc theo nhân viên, loại dịch vụ.
  - [ ] Thời gian xử lý truy vấn báo cáo < 1.5 giây theo NFR-01.
  - [ ] Xuất dữ liệu ra file Excel (.xlsx) chuẩn biểu mẫu kế toán.

#### **Issue #11: [FR-05.2] Dashboard tổng hợp số liệu trực quan cho Ban Lãnh đạo**
- **Labels:** `module:05-reporting`, `priority:high`, `enhancement`
- **Milestone:** `Sprint 5 - Analytics & Release`
- **Nhánh phát triển:** `feature/FR-05_reporting-analytics`
- **Mô tả (User Story):**
  > Là **Ban Giám đốc**, tôi muốn có màn hình Dashboard hiển thị biểu đồ trực quan về doanh thu thời gian thực, tỷ lệ tăng trưởng và chỉ số KPI vận hành.
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [ ] Biểu đồ đường (Line chart) doanh thu theo chu kỳ.
  - [ ] Biểu đồ tròn (Pie chart) tỷ trọng các loại dịch vụ.
  - [ ] Tỷ lệ xử lý sự cố thành công và đánh giá mức độ hài lòng khách hàng.
