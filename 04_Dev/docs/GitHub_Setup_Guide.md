# 🌐 HƯỚNG DẪN KẾT NỐI & VẬN HÀNH DỰ ÁN TRÊN GITHUB

Tài liệu này hướng dẫn chi tiết cách đưa dự án **PROJECT_1** lên GitHub, kích hoạt quy trình GitFlow chuẩn và tự động hóa quản lý các chức năng từ SRS lên **GitHub Issues & Projects**.

---

## 🌳 1. MÔ HÌNH NHÁNH GITFLOW ĐÃ ĐƯỢC THIẾT LẬP CỤC BỘ

Dự án đã được phân bổ thành các nhánh tương ứng với 5 phân hệ chức năng trong `02_BA/SRS.md`:

```text
main (Nhánh Production ổn định nhất)
  └── develop (Nhánh tích hợp & kiểm thử Staging)
        ├── feature/FR-01_user-role-management      # Phân hệ 1: Người dùng & Danh mục
        ├── feature/FR-02_core-business-operations  # Phân hệ 2: Nghiệp vụ cốt lõi (FR-CORE-01)
        ├── feature/FR-03_payment-finance           # Phân hệ 3: Thanh toán & Tài chính
        ├── feature/FR-04_incident-handling         # Phân hệ 4: Xử lý sự cố & Khiếu nại
        └── feature/FR-05_reporting-analytics       # Phân hệ 5: Báo cáo & Thống kê
```

---

## 🚀 2. HƯỚNG DẪN LIÊN KẾT & PUSH LÊN GITHUB (3 BƯỚC ĐƠN GIẢN)

### Bước 1: Tạo Repository mới trên GitHub
1. Truy cập: [https://github.com/new](https://github.com/new).
2. Đặt tên Repository (ví dụ: `PROJECT_1` hoặc tên dự án của bạn).
3. **Lưu ý quan trọng:** **KHÔNG** tích chọn *"Add a README file"*, *"Add .gitignore"*, hoặc *"Choose a license"* (vì dự án cục bộ đã có đầy đủ cấu trúc chuẩn).
4. Nhấn nút **Create repository**.

### Bước 2: Liên kết Remote vào dự án cục bộ
Mở PowerShell tại thư mục `d:\PTPMHĐT\PROJECT_1` và chạy lệnh sau (thay URL bằng link repo vừa tạo):

```powershell
git remote add origin https://github.com/<tai-khoan-github-cua-ban>/<ten-repo>.git
```

### Bước 3: Đẩy (Push) toàn bộ các nhánh lên GitHub
Đẩy toàn bộ các nhánh `main`, `develop` và 5 nhánh `feature/*`:

```powershell
# Đẩy toàn bộ nhánh
git push -u origin --all

# Đẩy thẻ phiên bản (tags) nếu có
git push -u origin --tags
```

---

## 🤖 3. TỰ ĐỘNG TẠO 11 ISSUES & MILESTONES LÊN GITHUB

Dự án đã tích hợp sẵn script tự động hóa tại `06_DevOps/scripts/create_github_issues.ps1`. Script này sẽ tự động:
1. Tạo 8 Labels chuẩn màu sắc theo từng phân hệ và mức độ ưu tiên.
2. Tạo 5 Milestones từ Sprint 1 đến Sprint 5.
3. Tạo đầy đủ 11 Issues từ tài liệu SRS kèm User Story và Acceptance Criteria chi tiết.

### Cách chạy:
1. Lấy GitHub Personal Access Token (PAT):
   - Truy cập: [GitHub Settings -> Developer Settings -> Personal access tokens](https://github.com/settings/tokens).
   - Chọn **Generate new token (classic)**, đặt tên và tick quyền `repo`.
2. Mở PowerShell trong thư mục dự án và chạy:
```powershell
.\06_DevOps\scripts\create_github_issues.ps1 -Repo "<tai-khoan>/<ten-repo>" -Token "<token_cua_ban>"
```
*Ví dụ:*
```powershell
.\06_DevOps\scripts\create_github_issues.ps1 -Repo "giabao/PROJECT_1" -Token "ghp_xxxxxxxxxxxxxxxxxxxx"
```

---

## 🛡️ 4. THIẾT LẬP BẢO VỆ NHÁNH (BRANCH PROTECTION RULES)
Để đảm bảo chất lượng phần mềm theo chuẩn doanh nghiệp:
1. Truy cập repo trên GitHub -> **Settings** -> **Branches** -> **Add branch protection rule**.
2. Áp dụng cho nhánh `main` và `develop`:
   - ✅ **Require a pull request before merging:** Bắt buộc mở Pull Request, không commit trực tiếp.
   - ✅ **Require approvals:** Yêu cầu ít nhất 1 thành viên review và phê duyệt.
   - ✅ **Require status checks to pass before merging:** Bắt buộc pipeline CI GitHub Actions phải PASS mới cho phép merge.

---

## 📌 5. CÁC TÀI NGUYÊN GITHUB ĐÃ TÍCH HỢP TRONG CODEBASE
- `.github/ISSUE_TEMPLATE/01_feature_request.md`: Mẫu tạo yêu cầu tính năng theo chuẩn SRS.
- `.github/ISSUE_TEMPLATE/02_bug_report.md`: Mẫu báo cáo lỗi theo chuẩn QA Bug Life Cycle.
- `.github/ISSUE_TEMPLATE/03_task_wbs.md`: Mẫu nhiệm vụ kỹ thuật theo phân rã WBS.
- `.github/PULL_REQUEST_TEMPLATE.md`: Mẫu PR chuẩn kiểm tra SOLID, Clean Code và Unit Tests.
- `.github/workflows/ci.yml`: Pipeline tự động kiểm tra cú pháp và rà quét bảo mật.
