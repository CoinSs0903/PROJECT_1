# 📋 BẢNG MA TRẬN PHÂN CÔNG TRÁCH NHIỆM (RACI MATRIX)

## 📌 Ý nghĩa các ký hiệu:
- **R - Responsible (Người thực hiện):** Người trực tiếp làm công việc và tạo ra sản phẩm.
- **A - Accountable (Người chịu trách nhiệm chính):** Người duy nhất có quyền quyết định và phê duyệt nghiệm thu công việc.
- **C - Consulted (Người được tham vấn):** Chuyên gia hoặc các bên liên quan được hỏi ý kiến đóng góp hai chiều.
- **I - Informed (Người được thông báo):** Những người cần được cập nhật tiến độ và kết quả (giao tiếp một chiều).

---

| Hoạt động dự án (Project Activities) | Project Manager (PM) | Business Analyst (BA) | Solution Architect / Tech Lead | Backend Developer | Frontend Developer | QA / QC Tester | DevOps Engineer | Khách Hàng / PO |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **1. Khởi tạo & Kế hoạch** | | | | | | | | |
| Lập Project Charter | **A / R** | C | C | I | I | I | I | C |
| Lập WBS & Lịch trình tiến độ | **A / R** | C | C | I | I | I | I | I |
| Dự toán chi phí & Phân bổ nhân sự | **A / R** | I | C | I | I | I | I | I |
| **2. Phân tích yêu cầu (BA)** | | | | | | | | |
| Khảo sát & Phỏng vấn người dùng | A | **R** | I | I | I | I | I | C |
| Soạn thảo tài liệu đặc tả SRS | A | **R** | C | I | I | C | I | C |
| Viết User Stories & Acceptance Criteria | A | **R** | C | I | I | C | I | C |
| Phê duyệt đặc tả yêu cầu | A | C | C | I | I | I | I | **A** |
| **3. Thiết kế & Kiến trúc** | | | | | | | | |
| Thiết kế Kiến trúc tổng thể hệ thống | A | C | **A / R** | C | C | I | C | I |
| Phân tích thiết kế hướng đối tượng (OOAD) | A | C | **A / R** | C | C | I | I | I |
| Thiết kế CSDL (ERD, Data Dictionary) | A | C | **A / R** | R | I | I | I | I |
| Thiết kế API Specification (Swagger) | A | I | **A** | **R** | C | C | I | I |
| Thiết kế UI/UX Prototype (Figma) | A | C | C | I | C | I | I | **C** |
| **4. Lập trình phát triển** | | | | | | | | |
| Khởi tạo Repo, Branching & Base Framework | A | I | **A / R** | C | C | I | C | I |
| Lập trình Backend API & Logic | A | I | A | **R** | I | I | I | I |
| Lập trình Giao diện Frontend/App | A | I | A | I | **R** | I | I | I |
| Viết Unit Tests | A | I | A | **R** | **R** | I | I | I |
| Code Review & Merge Pull Request | A | I | **A / R** | C | C | I | I | I |
| **5. Kiểm thử chất lượng (QA/QC)** | | | | | | | | |
| Soạn thảo Test Plan | A | C | C | I | I | **A / R** | I | I |
| Viết Test Cases & Test Scenarios | A | C | I | I | I | **A / R** | I | I |
| Thực hiện Manual & Automation Testing | A | I | I | C | C | **A / R** | I | I |
| Log Bug và theo dõi xử lý Bug | A | I | I | R | R | **A / R** | I | I |
| Kiểm thử nghiệm thu người dùng (UAT) | A | R | I | I | I | C | I | **A / R** |
| **6. DevOps & Triển khai** | | | | | | | | |
| Cấu hình CI/CD Pipelines | A | I | C | I | I | I | **A / R** | I |
| Thiết lập môi trường Dev / Staging / Prod | A | I | C | I | I | I | **A / R** | I |
| Cấu hình Giám sát (Monitoring & Logging) | A | I | C | I | I | I | **A / R** | I |
| **7. Phát hành & Vận hành** | | | | | | | | |
| Đóng gói phiên bản & Release Notes | **A / R** | I | C | I | I | C | R | I |
| Soạn thảo User Manual & Hướng dẫn kỹ thuật | A | **R** | C | C | C | I | I | I |
| Đào tạo người dùng & Bàn giao sản phẩm | **A** | **R** | C | I | I | I | I | C |
| Hỗ trợ vận hành & Vá lỗi bảo trì (L1-L3) | A | I | C | R | R | C | R | I |
