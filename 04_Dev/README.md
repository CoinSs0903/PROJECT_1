# 📁 04_Dev (Lập Trình & Phát Triển Phần Mềm)

## 📌 1. Mục Đích & Vai Trò
Thư mục này là nơi chứa toàn bộ mã nguồn của hệ thống phần mềm, các tài liệu hướng dẫn lập trình, kịch bản khởi chạy và công cụ hỗ trợ phát triển.
- **Trách nhiệm chính (Owner):** Tech Lead, Senior Developers, Backend/Frontend/Fullstack Developers.
- **Phối hợp cùng:** Solution Architect, QA/QC, DevOps.

---

## 📂 2. Cấu Trúc Thư Mục Con
```text
04_Dev/
├── docs/                       # Tài liệu hướng dẫn lập trình: Coding Conventions, GitFlow, Setup Guide
│   └── GitFlow_Conventions.md
├── src/                        # Mã nguồn ứng dụng (Source code)
│   ├── backend/                # Mã nguồn Backend API / Microservices (.NET / Spring / Node / Python)
│   │   ├── Controllers/        # Lớp tiếp nhận Request
│   │   ├── Services/           # Lớp xử lý nghiệp vụ chính
│   │   ├── Repositories/       # Lớp tương tác CSDL
│   │   ├── Models/             # Domain Entities
│   │   └── DTOs/               # Data Transfer Objects
│   ├── frontend/               # Mã nguồn Giao diện người dùng (React / Vue / Angular)
│   │   ├── src/components/     # UI Components dùng chung
│   │   ├── src/pages/          # Màn hình chức năng
│   │   ├── src/services/       # Gọi API backend qua Axios / Fetch
│   │   └── src/store/          # Quản lý trạng thái (Redux / Pinia)
│   └── database/               # Kịch bản cơ sở dữ liệu: DDL, Migrations, Seeds
├── scripts/                    # Kịch bản tiện ích: Seed dữ liệu test, migrate DB
└── README.md                   # Hướng dẫn tổng thể cho Developer mới gia nhập dự án
```

---

## 📋 3. Quy Trình Làm Việc Hàng Ngày Của Lập Trình Viên (Developer Workflow)
1. **Tiếp nhận Task trên Jira/GitHub Issues:**
   - Đọc kỹ mô tả User Story và tiêu chí chấp nhận (**Acceptance Criteria - AC**).
   - Kiểm tra kỹ thiết kế tương ứng trong `03_Design` (ERD, API Specs, Figma UI).
2. **Quy trình tạo Branch:**
   - Kéo code mới nhất từ nhánh `develop`: `git checkout develop && git pull origin develop`.
   - Tạo nhánh theo cú pháp chuẩn: `feature/TASK-ID_ten-tinh-nang` hoặc `bugfix/BUG-ID_ten-loi`.
3. **Lập trình và Viết Unit Test:**
   - Lập trình tính năng tuân thủ quy chuẩn Clean Code & SOLID principles.
   - Bắt buộc viết **Unit Test** với độ bao phủ (Coverage) tối thiểu đạt **75%**.
4. **Kiểm tra chất lượng trước khi nộp (Pre-commit Checklist):**
   - Chạy linter tự động: `npm run lint` hoặc `dotnet format`.
   - Chạy toàn bộ test suite cục bộ: `npm test` hoặc `dotnet test`.
5. **Tạo Pull Request (PR) & Code Review:**
   - Tạo PR vào nhánh `develop`.
   - Gắn tag Reviewer (Bắt buộc tối thiểu 1 Senior/Tech Lead duyệt).
   - Pipeline CI tự động chạy kiểm tra build và SonarQube Quality Gate.
   - Sau khi đủ điều kiện, tiến hành Merge nhánh và chuyển Task sang trạng thái **"Ready for QA"**.
