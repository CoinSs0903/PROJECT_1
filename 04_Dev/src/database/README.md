# 🗄️ Database Scripts & Migrations

Thư mục lưu trữ các kịch bản khởi tạo, cập nhật và nạp dữ liệu cho cơ sở dữ liệu.

### Cấu Trúc Khuyến Nghị:
- `migrations/` : Các file migration tăng dần theo thời gian (ví dụ: `V1__init_schema.sql`, `V2__add_index_users.sql`).
- `seeds/` : Dữ liệu mẫu dùng cho môi trường Development và Testing.
- `procedures/` : Các Stored Procedures, Functions, Triggers nếu có.
- `backups/` : Kịch bản sao lưu dữ liệu định kỳ.
