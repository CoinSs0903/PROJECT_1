# 📁 01_PM (Quản Lý Dự Án)

## 📌 1. Mục Đích & Vai Trò
Thư mục này chịu trách nhiệm lưu trữ toàn bộ các tài liệu điều hành, lập kế hoạch, kiểm soát ngân sách, quản lý tiến độ, rủi ro và các cuộc họp của dự án.
- **Trách nhiệm chính (Owner):** Project Manager (PM), Scrum Master, Project Management Office (PMO).
- **Phối hợp cùng:** Product Owner (PO), Tech Lead, Khách hàng / Ban giám đốc.

---

## 📂 2. Cấu Trúc Thư Mục Con
```text
01_PM/
├── 01_Charter/         # Bản điều lệ dự án, mục tiêu dự án, phê duyệt ngân sách
├── 02_Schedule/        # Sơ đồ Gantt tiến độ, Sprint Backlogs
├── 03_Resource/        # Phân bổ nguồn lực nhân sự, quản lý chi phí
├── 04_Risk_Issues/     # Bảng theo dõi rủi ro (Risk Register), nhật ký xử lý phát sinh (Issue Log)
├── 05_Meetings/        # Biên bản họp Kick-off, Daily Stand-up, Sprint Planning, Retrospective
├── README.md           # Hướng dẫn quy trình quản lý dự án
├── WBS.md              # Bảng phân rã cấu trúc công việc WBS chi tiết
└── RACI.md             # Ma trận phân nhiệm trách nhiệm phòng ban
```

---

## 📋 3. Danh Mục Các Đầu Việc Cụ Thể (Key Tasks)
1. **Khởi tạo dự án (Initiation Phase):**
   - Tiếp nhận bài toán, khảo sát tính khả thi (Feasibility Study).
   - Soạn thảo và ký duyệt **Project Charter** (Xác định mục tiêu, phạm vi High-Level, thời hạn, ngân sách).
   - Tổ chức cuộc họp khởi động dự án (**Kick-off Meeting**).
2. **Lập kế hoạch (Planning Phase):**
   - Xây dựng bảng phân rã công việc **WBS** (Work Breakdown Structure).
   - Ước lượng thời gian (Estimation) và lập lịch trình triển khai (**Master Schedule / Roadmap**).
   - Xác định quy trình làm việc (Scrum, Kanban hoặc Waterfall kết hợp) và định nghĩa DoD (Definition of Done).
   - Lập kế hoạch quản trị rủi ro (**Risk Management Plan**).
3. **Thực thi và Giám sát (Executing & Monitoring Phase):**
   - Theo dõi tiến độ Sprint (Burn-down Chart, Kanban board trên Jira/Trello).
   - Điều phối nguồn lực và gỡ rối khó khăn kỹ thuật/nghiệp vụ (Blockers/Impediments).
   - Báo cáo định kỳ tình hình dự án (Weekly/Monthly Status Report) gửi Khách hàng & Ban giám đốc.
   - Quản lý các yêu cầu thay đổi phạm vi (**Change Request - CR**).
4. **Đóng dự án (Closing Phase):**
   - Tổ chức nghiệm thu tổng thể (Project Sign-off).
   - Họp tổng kết rút kinh nghiệm (**Retrospective / Post-Mortem**).
   - Lưu trữ toàn bộ hồ sơ dự án và giải phóng nhân sự.
