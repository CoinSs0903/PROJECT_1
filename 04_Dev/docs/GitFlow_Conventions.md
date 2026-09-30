# 💻 QUY CHUẨN LẬP TRÌNH & CHIẾN LƯỢC QUẢN LÝ NHÁNH GIT (GITFLOW)

## 1. QUY ƯỚC ĐẶT TÊN & CODING CONVENTIONS
- **Classes, Interfaces, Structs:** Sử dụng `PascalCase` (ví dụ: `UserController`, `IOrderService`).
- **Methods, Functions:** Sử dụng `camelCase` hoặc `PascalCase` (tùy ngôn ngữ C# dùng PascalCase, JS/TS/Java dùng camelCase: `getUserById()`, `CalculateTotal()`).
- **Variables, Parameters:** Sử dụng `camelCase` (ví dụ: `customerId`, `orderDate`).
- **Constants:** Sử dụng `UPPER_SNAKE_CASE` (ví dụ: `MAX_RETRY_ATTEMPTS = 3`).
- **Database Tables & Columns:** Sử dụng `snake_case` số nhiều (ví dụ: `table: users`, `column: created_at`).

### Nguyên Tắc Lập Trình (Software Engineering Principles):
1. **SOLID Principles:**
   - **S (Single Responsibility):** Mỗi class chỉ chịu trách nhiệm cho một việc duy nhất.
   - **O (Open/Closed):** Mở rộng tính năng bằng kế thừa/interface, hạn chế sửa code cũ đã ổn định.
   - **L (Liskov Substitution):** Các class con có thể thay thế hoàn toàn cho class cha.
   - **I (Interface Segregation):** Tách nhiều interface nhỏ thay vì 1 interface quá lớn.
   - **D (Dependency Inversion):** Phụ thuộc vào trừu tượng (Abstraction/Interface), không phụ thuộc trực tiếp vào triển khai cụ thể (Concretion). Sử dụng Dependency Injection (DI).
2. **DRY (Don't Repeat Yourself):** Không viết lặp lại mã nguồn; trừu tượng hóa các hàm dùng chung vào thư mục `shared` hoặc `utils`.
3. **KISS (Keep It Simple, Stupid):** Giữ logic đơn giản, mạch lạc, dễ đọc.

---

## 2. CHIẾN LƯỢC QUẢN LÝ NHÁNH GIT (GITFLOW STRATEGY)

```text
main / master   ───●────────────────────────● (v1.0.0 Release)
                   \                       /
release/v1.0.0      \─────────●───────────●
                     \       /
develop         ──────●─────●───────●────── (Development branch)
                       \   /       /
feature/TASK-101        ──●       /
                                 /
feature/TASK-102        ────────●
```

### Các Nhánh Chính:
1. `main` (hoặc `master`): Nhánh chứa mã nguồn sản phẩm chính thức đang chạy trên môi trường **Production**. Chỉ cập nhật thông qua Merge từ `release` hoặc `hotfix`.
2. `develop`: Nhánh tích hợp chính dành cho việc kiểm thử và chạy trên môi trường **Staging / Test**. Mọi tính năng mới đều phải được merge vào đây.

### Các Nhánh Hỗ Trợ:
- `feature/<ticket-id>_<ten-tinh-nang>`: Tách từ `develop`, dùng để phát triển 1 tính năng cụ thể.
- `bugfix/<ticket-id>_<ten-loi>`: Tách từ `develop`, dùng để sửa lỗi phát hiện trong quá trình test.
- `release/vX.Y.Z`: Tách từ `develop` khi chuẩn bị phát hành phiên bản mới để QA nghiệm thu lần cuối.
- `hotfix/vX.Y.Z`: Tách trực tiếp từ `main` khi có sự cố khẩn cấp trên Production cần vá ngay lập tức.

### Quy Chuẩn Commit Message (Conventional Commits):
Cú pháp: `<type>(<scope>): <mô tả ngắn gọn>`
- `feat`: Thêm tính năng mới (ví dụ: `feat(auth): implement JWT login endpoint`)
- `fix`: Sửa lỗi (ví dụ: `fix(cart): prevent negative quantity in checkout`)
- `refactor`: Tái cấu trúc mã nguồn không làm thay đổi tính năng
- `test`: Viết thêm hoặc sửa unit test
- `docs`: Cập nhật tài liệu
- `chore`: Cập nhật dependency, cấu hình build
