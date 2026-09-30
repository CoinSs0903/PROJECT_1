# 📁 05_QA (Đảm Bảo Chất Lượng & Kiểm Thử)

## 📌 1. Mục Đích & Vai Trò
Thư mục này chịu trách nhiệm kiểm tra toàn diện phần mềm, bảo đảm mọi tính năng hoạt động đúng theo đặc tả nghiệp vụ trong `02_BA`, không có lỗ hổng bảo mật và đạt chuẩn chất lượng trước khi bàn giao cho khách hàng.
- **Trách nhiệm chính (Owner):** QA Lead, QC / Manual Tester, Automation Test Engineer, Security Tester.
- **Phối hợp cùng:** Business Analyst, Developers, DevOps, Khách hàng.

---

## 📂 2. Cấu Trúc Thư Mục Con
```text
05_QA/
├── 01_Test_Plan/             # Kế hoạch kiểm thử tổng thể (Master Test Plan: phạm vi, lịch trình, tiêu chí nghiệm thu)
├── 02_Test_Cases/            # Kịch bản kiểm thử chi tiết (Functional, UI, Integration, Boundary, Negative test cases)
├── 03_Automation/            # Kịch bản kiểm thử tự động (Selenium / Playwright / Cypress / Postman Collections)
├── 04_Performance/           # Kết quả kiểm thử hiệu năng (JMeter / k6) và bảo mật (OWASP ZAP)
├── 05_Bugs/                  # Báo cáo theo dõi lỗi (Defect logs, thống kê lỗi, Root Cause Analysis)
├── 06_UAT/                   # Biên bản kiểm thử chấp nhận người dùng (User Acceptance Testing)
├── README.md                 # Hướng dẫn quy trình QA/QC
└── TestPlan_BugWorkflow.md   # Quy trình kiểm thử và vòng đời xử lý Bug
```

---

## 📋 3. Danh Mục Các Đầu Việc Cụ Thể Của Đội Kiểm Thử
1. **Lập Kế hoạch kiểm thử (Test Planning):**
   - Phân tích tài liệu yêu cầu (SRS) từ BA để xác định phạm vi kiểm thử (In-scope) và các phần loại trừ (Out-of-scope).
   - Lập tài liệu **Test Plan** quy định môi trường test, công cụ kiểm thử, tiêu chí Pass/Fail và tiêu chuẩn bàn giao.
2. **Thiết kế Kịch bản kiểm thử (Test Design):**
   - Viết **Test Cases** với đầy đủ các trường: Test Case ID, Mục tiêu kiểm thử, Tiền điều kiện (Preconditions), Các bước thực hiện (Steps), Dữ liệu kiểm thử (Test Data), Kết quả mong đợi (Expected Results).
   - Thiết kế các ca kiểm thử biên (Boundary Value Analysis), phân vùng tương đương (Equivalence Partitioning) và ca kiểm thử tiêu cực (Negative Cases).
3. **Thực thi kiểm thử (Test Execution):**
   - **Smoke Testing:** Kiểm tra nhanh bản dựng (Build) mới xem các chức năng cốt lõi có chạy được không trước khi test sâu.
   - **Functional Testing:** Kiểm thử chi tiết từng màn hình, từng luồng nghiệp vụ.
   - **Regression Testing (Kiểm thử hồi quy):** Kiểm tra lại toàn bộ hệ thống sau khi Developer sửa bug để bảo đảm không phát sinh lỗi mới ở các phần đã chạy tốt.
   - **API Testing:** Sử dụng Postman / Newman kiểm thử tính toàn vẹn của các REST API.
4. **Quản lý & Theo dõi lỗi (Defect Tracking):**
   - Ghi nhận lỗi lên hệ thống quản lý (Jira / Redmine) với mức độ nghiêm trọng (**Severity**) và độ ưu tiên (**Priority**).
   - Đính kèm đầy đủ log, ảnh chụp màn hình, video và các bước tái hiện (Steps to Reproduce).
   - Xác minh lỗi (Verify Bug) sau khi Developer đẩy bản sửa lỗi.
5. **Nghiệm thu người dùng (UAT) & Báo cáo chất lượng:**
   - Hướng dẫn và phối hợp với khách hàng/người dùng cuối thực hiện kịch bản kiểm thử nghiệm thu UAT.
   - Xuất Báo cáo tổng kết kiểm thử (**Test Summary Report**) xác nhận hệ thống đủ điều kiện phát hành (Release Readiness).
