# 📁 03_Design (Thiết Kế Hệ Thống, OOAD & UI/UX)

## 📌 1. Mục Đích & Vai Trò
Thư mục này đóng vai trò chuyển hóa các yêu cầu từ BA (`02_BA`) thành bản vẽ kỹ thuật chi tiết phục vụ cho đội lập trình (`04_Dev`) và kiểm thử (`05_QA`).
- **Trách nhiệm chính (Owner):** Solution Architect (SA), Tech Lead, Data Architect, UI/UX Designer.
- **Phối hợp cùng:** Business Analyst, Frontend/Backend Developers, DevOps.

---

## 📂 2. Cấu Trúc Thư Mục Con
```text
03_Design/
├── 01_Architecture/      # Kiến trúc hệ thống tổng thể (HLD/LLD, Clean Architecture, Layered, Microservices)
├── 02_OOAD_UML/          # Phân tích & thiết kế hướng đối tượng: Use Case, Class, Sequence, Activity Diagram
├── 03_Database/          # Thiết kế CSDL: Sơ đồ ERD, Data Dictionary, DDL scripts, Indexes
├── 04_API/               # Thiết kế hợp đồng API: OpenAPI/Swagger JSON/YAML, RESTful endpoints, DTOs
├── 05_UI_UX/             # Wireframes, Flowcharts người dùng, Design System, link file Figma/Mockup
├── README.md             # Hướng dẫn quy chuẩn thiết kế
└── Architecture_OOAD.md  # Bản mô tả chi tiết mẫu thiết kế OOAD & Kiến trúc
```

---

## 📋 3. Danh Mục Các Đầu Việc Cụ Thể
1. **Thiết kế kiến trúc hệ thống (System Architecture):**
   - Lựa chọn mô hình kiến trúc (Monolithic, Modular Monolith, Clean Architecture hoặc Microservices).
   - Lựa chọn Tech Stack (Ngôn ngữ lập trình, Frameworks, CSDL quan hệ SQL, In-memory Caching Redis, Message Broker Kafka/RabbitMQ).
   - Thiết kế luồng xử lý và các cơ chế bảo mật (Authen/Author JWT, API Gateway, Rate Limiting).
2. **Phân tích & Thiết kế Hướng đối tượng (OOAD - Object-Oriented Analysis & Design):**
   - **Use Case Diagrams & Use Case Specifications:** Phân tích hành vi tương tác của từng Actor với hệ thống.
   - **Class Diagrams:** Thiết kế các lớp đối tượng, thuộc tính, phương thức, quan hệ kế thừa (Inheritance), quan hệ kết tập (Aggregation/Composition), quan hệ phụ thuộc (Dependency).
   - **Sequence Diagrams (Biểu đồ tuần tự):** Thiết kế luồng truyền thông điệp giữa các đối tượng theo thời gian cho từng Use Case trọng yếu.
   - **State Machine & Activity Diagrams:** Mô tả vòng đời trạng thái của các thực thể quan trọng (Đơn hàng: Mới tạo -> Đang duyệt -> Đang xử lý -> Hoàn thành / Hủy bỏ).
3. **Thiết kế Cơ sở dữ liệu (Database Design):**
   - Xây dựng mô hình thực thể liên kết (ERD - Entity Relationship Diagram) từ mức Quan niệm (Conceptual), Logic (Logical) đến Vật lý (Physical).
   - Chuẩn hóa CSDL đạt chuẩn 3NF (Third Normal Form) để tránh dư thừa dữ liệu và bất thường khi cập nhật.
   - Thiết kế Index, Khóa chính (Primary Key), Khóa ngoại (Foreign Key) và tối ưu hóa hiệu năng truy vấn.
4. **Thiết kế API (API Contract Design):**
   - Xây dựng tài liệu OpenAPI / Swagger chuẩn RESTful.
   - Định nghĩa cấu trúc chuẩn của Request Body, Response Code (200, 201, 400, 401, 403, 404, 500) và Error Payload format.
5. **Thiết kế Trải nghiệm & Giao diện người dùng (UI/UX Design):**
   - Xây dựng User Journey Map và Wireframe các màn hình chính.
   - Xây dựng UI Design trên Figma kèm Design System (Màu sắc, Typography, Spacing, Buttons, Inputs, Tables).
