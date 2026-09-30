# 🚀 Backend Source Code Directory

Thư mục lưu trữ mã nguồn phía máy chủ (Server-side API / Microservices).

### Cấu Trúc Khuyến Nghị:
- `src/Controllers/` : Chứa các API Endpoints tiếp nhận Request và trả về Response.
- `src/Services/` : Chứa Logic nghiệp vụ cốt lõi (Business Logic).
- `src/Repositories/` : Lớp Data Access Object thao tác trực tiếp với Database qua ORM / SQL.
- `src/Models/` : Domain Entities phản chiếu các bảng dữ liệu.
- `src/DTOs/` : Data Transfer Objects định nghĩa dữ liệu trao đổi giữa Client và Server.
- `src/Middlewares/` : Xử lý Authentication, Logging, Global Exception Handling.
- `tests/` : Unit Tests và Integration Tests.
