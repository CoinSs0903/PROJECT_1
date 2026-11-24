# 📘 BẢN PHÂN TÍCH HỒ SƠ THIẾT KẾ & CẨM NANG HƯỚNG DẪN HOÀN THIỆN ĐỒ ÁN (PROJECT 1)

> **Chuyên gia thẩm định:** Senior Software Design Dossier Analyst  
> **Dự án:** Hệ Thống Đấu Giá Trực Tuyến Tự Động (Online Auction Platform)  
> **Đối chiếu học phần:** Phát Triển Phần Mềm Hướng Đối Tượng (OOAD / PTPMHĐT)

---

## PHẦN 1: ĐÁNH GIÁ CHUYÊN SÂU HIỆN TRẠNG DỰ ÁN

### 1.1. Những điểm nhóm đang làm RẤT TỐT (ĐẠT CHUẨN DOANH NGHIỆP)
1. **Nghiệp vụ có chiều sâu kinh doanh (Không phải CRUD đơn thuần):**
   - Thuật toán **Proxy Bidding (Đấu giá tự động)** giải quyết triệt để nỗi đau người mua phải canh màn hình 24/7.
   - Cơ chế **Reserve Price (Giá sàn bảo lưu ẩn)** bảo vệ người bán không bị bán rẻ món hàng.
   - Tính năng **Buy It Now (Mua ngay)** kết thúc phiên lập tức khi người mua trả mức giá cố định.
   - Cơ chế **Ví Escrow (Ký quỹ bảo chứng)** giữ tiền an toàn tuyệt đối, chỉ giải ngân 95% sau khi người mua xác nhận đã nhận hàng.
2. **Cơ sở dữ liệu chuẩn hóa xuất sắc:**
   - 15 bảng chuẩn hóa 3NF; tách biệt rõ `proxy_bids` (giá trần bí mật tối đa) và `bids` (lịch sử bước giá công khai).
   - Ràng buộc tài chính an toàn: dùng `BIGINT` đơn vị VNĐ (không sai số dấu phẩy động) và `CHECK (available_balance >= 0)`.
3. **Bộ kiểm thử tự động (Unit Tests) hoàn chỉnh:**
   - 12 test suites JUnit 5 bao phủ Happy path, Boundary và Exception.
4. **Kiến trúc Modular Monolith:**
   - Các phân hệ giao tiếp qua Facade (`WalletFacade`, `NotificationFacade`) và Domain Events.

---

### 1.2. Những yếu tố đang THIẾU cần BỔ SUNG NGAY trong hồ sơ nộp
1. **Sơ đồ Lớp (Class Diagrams) — *Thiếu hoàn toàn*:**
   - Thiếu **01 Sơ đồ lớp phân tích (Analysis/Domain Class Diagram)**: mô hình hóa thực thể nghiệp vụ độc lập công nghệ.
   - Thiếu **01 Sơ đồ lớp thiết kế (Design Class Diagram)**: mô hình hóa các lớp Controller, Facade, Service, Repository, Entity, DTO trong Spring Boot.
2. **Sơ đồ Hoạt động (Activity Diagrams) — *Thiếu hoàn toàn*:**
   - Cần bổ sung $\ge 2$ sơ đồ hoạt động có rẽ nhánh chi tiết: Quy trình so khớp Proxy Bidding và Quy trình Đóng phiên / Escrow / Relist.
3. **Sơ đồ Kiến trúc phân tầng (Architecture Diagrams):**
   - Bổ sung sơ đồ Kiến trúc Logic (Logical Layers) và Sơ đồ Triển khai (Deployment 3-Tier View).
4. **Ma trận Phân quyền & Ma trận Truy vết:**
   - Bổ sung **Role-Permission Matrix** ánh xạ quyền của 4 Roles (`Guest`, `Buyer`, `Seller`, `Admin`).
   - Bổ sung **Requirements Traceability Matrix (RTM)** kết nối Requirement $ightarrow$ Use Case $ightarrow$ Class $ightarrow$ DB $ightarrow$ API $ightarrow$ Test.

---

### 1.3. Những yếu tố DƯ THỪA / MÂU THUẪN cần LOẠI BỎ NGAY
1. **Xóa hoặc cập nhật lại 2 file sơ đồ database cũ:**
   - `PROJECT_1/docs/database_diagram.html`
   - `PROJECT_1/docs/database_diagram.drawio`  
   *(Hai file này lưu schema 19 bảng cũ, có `seller_profiles`, `holidays`... và thiếu `proxy_bids`).*
2. **Sửa dòng text ở footer web:**
   - File `PROJECT_1/frontend/web_app/index.html` dòng 61: Xóa dòng chữ *"Tuân thủ Luật Đấu giá tài sản số 01/2016/QH14..."*.
3. **Lược bỏ bớt phần so sánh quá khứ:**
   - Trong file `PROJECT_1/docs/ERD_EXPLANATION_AND_COMPARISON.md`, chỉ giữ lại phần giải thích 15 bảng hiện tại và bộ câu hỏi vấn đáp.

---

## PHẦN 2: CẨM NANG CHO TỪNG THÀNH VIÊN KHI ĐỌC HỒ SƠ & BẢO VỆ ĐỒ ÁN

### 👤 Thành viên 1: Business Analyst / Product Owner (BA)
* **Trách nhiệm trong hồ sơ:** Chương 1 (Overview, Scope), Chương 2 (BRD/SRS, Business Rules).
* **Kiến thức bắt buộc phải nắm:**
  * Tại sao bỏ tiền cọc trước? $ightarrow$ Trả lời: Bỏ cọc 5%--20% để xóa rào cản tham gia (Instant Bidding), tăng thanh khoản cho sàn; chống bùng hàng bằng phạt 3 gậy vi phạm (Unpaid Strikes) sẽ khóa tài khoản vĩnh viễn; bảo vệ người bán bằng quyền Relist.
  * Luật bảo lưu giá sàn ẩn hoạt động thế nào? $ightarrow$ Phiên chỉ bán thành công nếu giá cao nhất $\ge$ Reserve Price.

### 👤 Thành viên 2: System & Software Designer
* **Trách nhiệm trong hồ sơ:** Chương 3 (Use Case, Activity, Sequence, State Machine), Chương 4 (Domain Model), Chương 5 (Architecture, ADR), Chương 6 (Design Class, Patterns).
* **Kiến thức bắt buộc phải nắm:**
  * Phân biệt Analysis Class và Design Class? $ightarrow$ Analysis Class mô hình hóa nghiệp vụ thuần túy; Design Class mô hình hóa mã nguồn Spring Boot với các phương thức, thuộc tính và design patterns.
  * Tại sao dùng Facade Pattern? $ightarrow$ Để đóng gói phân hệ Ví (`WalletFacade`), chống phụ thuộc vòng giữa module đấu giá và tài chính.

### 👤 Thành viên 3: Data, API & UI/UX Engineer
* **Trách nhiệm trong hồ sơ:** Chương 7 (Database Design), Chương 8 (UI/UX), Chương 9 (Interface & API), Chương 11 (Deployment).
* **Kiến thức bắt buộc phải nắm:**
  * Tại sao tách `proxy_bids` và `bids`? $ightarrow$ `proxy_bids` chứa giá trần bí mật (Max Bid) chỉ server biết; `bids` là bước giá công khai hiển thị cho mọi người xem.
  * Tại sao dùng `BIGINT` thay vì `DOUBLE`? $ightarrow$ Để loại trừ hoàn toàn lỗi sai số làm tròn dấu phẩy động trong tiền tệ.
  * Giải quyết xung đột đặt giá mili-giây cuối bằng cách nào? $ightarrow$ Dùng Khóa bi quan Database: `SELECT ... FOR UPDATE` trên bảng `auctions`.

### 👤 Thành viên 4: QA / Test & Documentation Engineer
* **Trách nhiệm trong hồ sơ:** Chương 0 (Document Control), Chương 10 (Security), Chương 12 (Testing, RTM), Chương 13 (Risk Log).
* **Kiến thức bắt buộc phải nắm:**
  * RTM (Requirements Traceability Matrix) là gì? $ightarrow$ Là ma trận truy vết chứng minh mọi yêu cầu chức năng đều có Use Case, Class thiết kế, API và Test Case tự động kiểm chứng.
  * Đã test những trường hợp nào? $ightarrow$ Happy path (đặt giá đầu tiên, vượt giá), Boundary (chạm giá sàn Reserve Price), Exception (tự đặt giá hàng của mình, rút tiền quá số dư ví, bùng hàng quá 72h).
