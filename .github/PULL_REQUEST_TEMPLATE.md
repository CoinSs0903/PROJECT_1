## 📌 1. LIÊN KẾT TICKET / ISSUE
- Liên kết Issue liên quan: Closes #... hoặc Fixes #...
- Phân hệ liên quan: `FR-01` / `FR-02` / `FR-03` / `FR-04` / `FR-05` / `WBS-...`

---

## 🏷️ 2. LOẠI THAY ĐỔI (TYPE OF CHANGE)
- [ ] `feat`: Tính năng mới đáp ứng yêu cầu SRS
- [ ] `fix`: Sửa lỗi theo kịch bản QA
- [ ] `refactor`: Tái cấu trúc code (không thay đổi hành vi nghiệp vụ)
- [ ] `docs`: Cập nhật tài liệu SRS, WBS, Kiến trúc, API
- [ ] `test`: Bổ sung Unit Test / Integration Test
- [ ] `chore`: Cấu hình hệ thống, CI/CD, Docker

---

## 📝 3. TÓM TẮT THAY ĐỔI
- Mô tả ngắn gọn những gì bạn đã làm trong Pull Request này:
  1. ...
  2. ...

---

## 🛡️ 4. BẢNG KIỂM TRA CHẤT LƯỢNG (CODE QUALITY CHECKLIST)
- [ ] **GitFlow:** Nhánh tạo đúng quy ước (`feature/...`, `bugfix/...`, `hotfix/...`) và merge vào nhánh `develop`.
- [ ] **Commit Message:** Đúng chuẩn Conventional Commits (`feat(...)`, `fix(...)`, v.v.).
- [ ] **SOLID & Clean Code:** Tuân thủ các nguyên tắc thiết kế hướng đối tượng, không hardcode, code rõ ràng dễ bảo trì.
- [ ] **Security:** Không commit dữ liệu nhạy cảm (`.env`, mật khẩu, Private Key, API Tokens).
- [ ] **Unit Tests:** Đã viết kiểm thử tự động, tất cả tests đều PASS với độ bao phủ > 75%.
- [ ] **Local Build:** Ứng dụng build và chạy thử thành công trên môi trường cục bộ, không phát sinh lỗi cảnh báo (0 Warnings, 0 Errors).

---

## 📸 5. HÌNH ẢNH HOẶC KẾT QUẢ KIỂM THỬ
*(Nếu có thay đổi UI hoặc kết quả test Postman API, đính kèm ảnh chụp tại đây)*
