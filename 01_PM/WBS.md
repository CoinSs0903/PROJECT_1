# 📊 WORK BREAKDOWN STRUCTURE (WBS) - PHÂN RÃ CÔNG VIỆC DỰ ÁN

Bảng phân rã chi tiết toàn bộ công việc từ cấp độ dự án đến cấp độ công việc chi tiết (Work Packages) theo chuẩn chuyên nghiệp:

| Mã WBS | Giai đoạn / Nhóm công việc | Nhiệm vụ cụ thể (Task) | Vị trí thực hiện | Sản phẩm bàn giao (Deliverable) |
|---|---|---|---|---|
| **1.0** | **Quản Lý Dự Án (Project Management)** | | | |
| 1.1 | Khởi tạo dự án | Lập điều lệ dự án, xác định Stakeholders, họp Kick-off | PM, Client | Project Charter, Meeting Notes |
| 1.2 | Lập kế hoạch tổng thể | Xây dựng WBS, Gantt Chart tiến độ, dự toán ngân sách | PM, Tech Lead | Master Plan, Resource Allocation |
| 1.3 | Quản trị điều hành | Tổ chức Daily Standup, Sprint Review, Retro, cập nhật Jira | Scrum Master, PM | Sprint Burn-down, Status Report |
| 1.4 | Quản lý rủi ro & Thay đổi | Giám sát rủi ro, đánh giá tác động thay đổi phạm vi | PM, Lead, BA | Risk Log, Change Requests (CR) |
| 1.5 | Nghiệm thu & Bàn giao | Tổ chức nghiệm thu giai đoạn, đóng dự án | PM, Client | Acceptance Certificate |
| **2.0** | **Phân Tích Nghiệp Vụ (Requirements & BA)** | | | |
| 2.1 | Khảo sát hiện trạng | Phỏng vấn người dùng, ghi nhận biểu mẫu, quy trình thực tế | BA | Interview Notes, Khảo sát nghiệp vụ |
| 2.2 | Đặc tả yêu cầu phần mềm | Soạn thảo BRD và SRS chuẩn IEEE 830 | BA, PO | Tài liệu SRS (Software Requirements) |
| 2.3 | Xây dựng Use Cases / User Stories | Phân tích Actor, Use Case Diagram, viết User Stories & AC | BA | Danh mục Use Cases, Product Backlog |
| 2.4 | Thẩm định & Duyệt yêu cầu | Review yêu cầu với khách hàng và đội phát triển | BA, PM, Client | SRS Sign-off |
| **3.0** | **Thiết Kế Hệ Thống & UI/UX (Design & Architecture)** | | | |
| 3.1 | Thiết kế kiến trúc tổng thể | Chọn kiến trúc hệ thống, Tech Stack, HLD/LLD | Solution Architect | Architectural Design Document (ADD) |
| 3.2 | Phân tích thiết kế OOAD | Vẽ Class Diagram, Sequence Diagram, State/Activity Diagram | Tech Lead, Dev | UML OOAD Documentation |
| 3.3 | Thiết kế Cơ sở dữ liệu | Thiết kế lược đồ ERD (Logical & Physical), Data Dictionary | Database Admin/Lead | Database Schema, Data Dictionary |
| 3.4 | Thiết kế API Specification | Đặc tả RESTful API, Schema Request/Response, OpenAPI | Tech Lead, Backend | Swagger/OpenAPI docs |
| 3.5 | Thiết kế Giao diện (UI/UX) | Wireframe, Flowchart, UI Mockup độ nét cao trên Figma | UI/UX Designer | Figma Prototype, Design System |
| **4.0** | **Lập Trình & Phát Triển (Development)** | | | |
| 4.1 | Setup môi trường & Project Core | Cấu hình Repository, Git Flow, Base Framework, Docker dev | Tech Lead | Repository Skeleton, Dev Guide |
| 4.2 | Phát triển Cơ sở dữ liệu | Viết Migration scripts, Seeding dữ liệu mẫu, Stored Procedure | Backend Dev | DB Migrations, Seed Scripts |
| 4.3 | Phát triển Backend Services | Lập trình Business Logic, Controllers, Services, Repositories | Backend Dev | RESTful API Endpoints |
| 4.4 | Phát triển Giao diện Frontend/App | Xây dựng Components, State Management, tích hợp API | Frontend Dev | Web App / Mobile App |
| 4.5 | Unit Test & Code Review | Viết Unit tests (độ bao phủ > 75%), Review Pull Requests | Dev, Tech Lead | Unit Test Results, PR Approvals |
| **5.0** | **Kiểm Thử & Đảm Bảo Chất Lượng (QA/QC Testing)** | | | |
| 5.1 | Lập kế hoạch kiểm thử | Xác định phạm vi test, chiến lược, môi trường, công cụ | QA Lead | Master Test Plan |
| 5.2 | Thiết kế Test Cases | Viết kịch bản kiểm thử Functional, Integration, Boundary | QC / Tester | Test Case Specification |
| 5.3 | Thực thi kiểm thử | Test Smoke, Sanity, Regression, ghi log bug lên Jira | QC / Tester | Execution Log, Bug Reports |
| 5.4 | Kiểm thử tự động & Phi chức năng | Viết Automation Script (Selenium/Playwright), Test tải | Automation Tester | Test Automation Suite, Load Report |
| 5.5 | Kiểm thử chấp nhận người dùng (UAT) | Hướng dẫn khách hàng test, khắc phục phản hồi | QA, BA, Client | UAT Report & Approval |
| **6.0** | **DevOps, Triển Khai & Hạ Tầng (DevOps & Infrastructure)** | | | |
| 6.1 | Quản trị hạ tầng đám mây / Máy chủ | Cấu hình máy chủ (Linux, Nginx), Networking, SSL | DevOps, Cloud Eng | Infrastructure as Code (IaC) |
| 6.2 | Thiết lập quy trình CI/CD | Pipeline tự động Build, Test, Security Scan, Deploy | DevOps | Jenkinsfile / GitHub Actions config |
| 6.3 | Containerization & Orchestration | Dockerize ứng dụng, cấu hình Docker Compose / K8s | DevOps | Dockerfile, K8s manifests |
| 6.4 | Cấu hình giám sát & Cảnh báo | Tích hợp Prometheus, Grafana, ELK/Loki log tracking | DevOps, SysAdmin | Monitoring Dashboards, Alert Bot |
| **7.0** | **Phát Hành, Đào Tạo & Bảo Trì (Release & Maintenance)** | | | |
| 7.1 | Đóng gói & Phát hành | Lập phiên bản (Tagging, SemVer), viết Release Notes | PM, Tech Lead | Release Package, Release Notes |
| 7.2 | Soạn thảo tài liệu người dùng | Viết Hướng dẫn sử dụng cho người dùng & Hướng dẫn cài đặt | BA, Tech Writer | User Manual, Admin Guide |
| 7.3 | Đào tạo chuyển giao | Tổ chức các buổi Training cho End-user và Đội vận hành | BA, PM, Client | Training Materials, Biên bản nghiệm thu |
| 7.4 | Vận hành & Hỗ trợ kỹ thuật | Xử lý ticket sự cố, vá lỗi nóng (Hotfixes), bảo trì định kỳ | L2/L3 Support, Dev | SLA Incident Reports, Maintenance Log |
