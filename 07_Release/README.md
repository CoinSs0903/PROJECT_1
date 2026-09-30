# 📁 07_Release (Phát Hành, Đào Tạo & Vận Hành Bảo Trì)

## 📌 1. Mục Đích & Vai Trò
Thư mục này quản lý toàn bộ hồ sơ đóng gói phát hành sản phẩm phần mềm, tài liệu đào tạo người dùng cuối, tài liệu quản trị hệ thống và nhật ký vận hành bảo trì sau khi phần mềm chính thức đi vào hoạt động (Go-Live).
- **Trách nhiệm chính (Owner):** Project Manager (PM), Technical Writer, L2/L3 Customer Support, Maintenance Operations Team.
- **Phối hợp cùng:** Khách hàng / End-users, Tech Lead, Developers, DevOps.

---

## 📂 2. Cấu Trúc Thư Mục Con
```text
07_Release/
├── 01_Release_Notes/       # Ghi chú phiên bản phát hành (Changelogs, tính năng mới, lỗi đã vá, version tagging)
├── 02_User_Manuals/        # Tài liệu hướng dẫn sử dụng phần mềm có hình ảnh minh họa cho người dùng cuối
├── 03_Admin_Guides/        # Tài liệu hướng dẫn quản trị viên (cấu hình phân quyền, backup/restore, cài đặt hệ thống)
├── 04_Training/            # Slide thuyết trình đào tạo, video demo nghiệp vụ, biên bản nghiệm thu đào tạo
├── 05_Maintenance_SLA/     # Báo cáo cam kết chất lượng dịch vụ (SLA), nhật ký xử lý sự cố (Incident Report)
├── README.md               # Hướng dẫn quy trình phát hành và bảo trì
└── Release_Notes.md        # Mẫu ghi chú phát hành phiên bản phần mềm
```

---

## 📋 3. Danh Mục Các Đầu Việc Cụ Thể
1. **Quy trình Phát hành Phiên bản (Software Release Process):**
   - Đánh dấu phiên bản mã nguồn theo chuẩn ngữ nghĩa **Semantic Versioning (SemVer: MAJOR.MINOR.PATCH)** (ví dụ: `v1.0.0`, `v1.1.0`, `v1.1.1`).
   - Biên soạn tài liệu **Release Notes** tóm lược: Tính năng mới (New Features), Cải tiến hiệu năng (Improvements), Lỗi đã khắc phục (Bug Fixes), Các thay đổi có khả năng gây xung đột (Breaking Changes).
   - Kiểm tra điều kiện tiên quyết (Release Checklist) trước khi chính thức kích hoạt môi trường Production (Go-Live).
2. **Biên soạn Tài liệu Bàn giao & Đào tạo:**
   - Soạn thảo tài liệu **User Manual (Hướng dẫn sử dụng)** với ngôn từ dễ hiểu, có ảnh chụp màn hình từng bước.
   - Soạn thảo tài liệu **Admin Guide (Hướng dẫn quản trị)** dành cho đội IT của khách hàng (quản lý tài khoản, phân quyền, cấu hình thông số, sao lưu dữ liệu).
   - Tổ chức các buổi đào tạo thực tế (Hands-on Training) cho nhân viên khách hàng và ký biên bản hoàn thành đào tạo.
3. **Quản trị Bảo hành & Vận hành Hỗ trợ (Maintenance & Support):**
   - Thiết lập các kênh tiếp nhận yêu cầu hỗ trợ (Ticketing System, Hotline, Email hỗ trợ).
   - Phân cấp hỗ trợ kỹ thuật theo chuẩn quốc tế:
     - **Level 1 (Helpdesk):** Tiếp nhận yêu cầu, giải đáp hướng dẫn sử dụng, phân loại vấn đề.
     - **Level 2 (Technical Support):** Kiểm tra log, phân tích dữ liệu, xử lý lỗi cấu hình hoặc dữ liệu sai lệch.
     - **Level 3 (Engineering / Dev):** Vá lỗi mã nguồn (Bug fixes), tối ưu hóa database, phát hành bản vá khẩn cấp (Hotfixes).
   - Định kỳ bảo trì hệ thống, dọn dẹp log, tối ưu hóa cơ sở dữ liệu và đánh giá rủi ro an toàn thông tin định kỳ.
