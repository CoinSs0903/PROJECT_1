# 📁 02_BA (Khảo Sát & Phân Tích Nghiệp Vụ)

## 📌 1. Mục Đích & Vai Trò
Thư mục này là cầu nối giữa bài toán kinh doanh của khách hàng và giải pháp kỹ thuật của đội ngũ phát triển phần mềm.
- **Trách nhiệm chính (Owner):** Business Analyst (BA), Product Owner (PO).
- **Phối hợp cùng:** Khách hàng / Stakeholders, Solution Architect, QA/QC, Tech Lead.

---

## 📂 2. Cấu Trúc Thư Mục Con
```text
02_BA/
├── 01_Surveys/         # Biên bản khảo sát thực tế, phiếu phỏng vấn, thu thập biểu mẫu/hồ sơ hiện trạng
├── 02_BRD/             # Business Requirements Document (Tài liệu yêu cầu kinh doanh cấp cao)
├── 03_SRS/             # Software Requirements Specification (Đặc tả chi tiết yêu cầu phần mềm - IEEE 830)
├── 04_User_Stories/    # Danh sách User Stories (kèm Acceptance Criteria), Use Case Scenarios
├── README.md           # Hướng dẫn quy trình nghiệp vụ BA
└── SRS.md              # Khung mẫu đặc tả yêu cầu phần mềm chuẩn chuyên nghiệp
```

---

## 📋 3. Danh Mục Các Đầu Việc Cụ Thể Của BA
1. **Khảo sát hiện trạng & Thu thập thông tin:**
   - Lập kế hoạch phỏng vấn người dùng thực tế (Phòng ban nghiệp vụ, khách hàng, nhân viên vận hành).
   - Thu thập tất cả các hồ sơ, biểu mẫu giấy tờ, chứng từ, báo cáo đang sử dụng thủ công (Ví dụ: Phiếu nhập, Phiếu xuất, Hóa đơn, Thẻ độc giả, Đơn xin gia hạn,...).
   - Xác định các vấn đề còn tồn đọng (Pain points), hạn chế của hệ thống cũ.
2. **Xác định phạm vi & Tác nhân hệ thống:**
   - Xác định Tác nhân ngoài (External Actors) và Tác nhân trong (Internal Roles).
   - Vẽ sơ đồ ngữ cảnh (Context Diagram) mô tả luồng thông tin vào/ra giữa hệ thống và các đối tượng bên ngoài.
3. **Phân rã chức năng (Functional Decomposition):**
   - Xây dựng Biểu đồ phân rã chức năng (BFD - Business Function Diagram) từ mức tổng quát (mức 0) xuống các chức năng lá (mức chi tiết).
   - Viết mô tả chi tiết cho từng chức năng lá: Điều kiện kích hoạt, Dữ liệu đầu vào, Dữ liệu đầu ra, Nơi sử dụng, Tần suất, Quy tắc nghiệp vụ (Business Rules).
4. **Xây dựng tài liệu SRS (Software Requirements Specification):**
   - Viết các yêu cầu chức năng (Functional Requirements - FR) theo mã định danh (FR-01, FR-02,...).
   - Viết các yêu cầu phi chức năng (Non-Functional Requirements - NFR): Hiệu năng (Performance), Bảo mật (Security), Khả năng mở rộng (Scalability), Tính khả dụng (Usability).
   - Soạn thảo Từ điển dữ liệu (Data Dictionary): Tên gọi, Ý nghĩa, Kiểu dữ liệu, Khuôn dạng, Ràng buộc dữ liệu.
5. **Thẩm định và Bàn giao yêu cầu:**
   - Tổ chức buổi Review yêu cầu (Walkthrough meeting) với Solution Architect, Tech Lead, Tester và Khách hàng.
   - Ký kết biên bản chốt phạm vi và bàn giao (Requirement Baseline & Sign-off).
