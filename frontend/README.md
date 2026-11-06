# 🌐 GIAO DIỆN NGƯỜI DÙNG (FRONTEND & WEB PORTAL)

> **Môn học:** Phát Triển Phần Mềm Hướng Đối Tượng (OOAD / PTPMHĐT)  
> **Kiến trúc giao diện:** Web Portal tích hợp Server-side Rendering (Thymeleaf, Bootstrap 5, Bootstrap Icons, Vanilla JS)

---

## 🏛️ GIAO DIỆN CHÍNH THỨC CỦA HỆ THỐNG (WEB PORTAL)

Toàn bộ giao diện tương tác chính thức của hệ thống được phục vụ trực tiếp bởi **Spring Boot Backend** thông qua engine **Thymeleaf**, đặt tại thư mục:
`backend/src/main/resources/templates/`

### 1. Các trang phân hệ chính:
* **Trang chủ & Sàn đấu giá ([index.html](file:///D:/PTPMH%C4%90T/PROJECT_1/backend/src/main/resources/templates/index.html)):**
  * Banner thương hiệu, 4 khối tính năng cốt lõi (*Proxy Bidding, Giá Sàn Bảo Lưu, Mua Ngay Buy It Now, Ví Bảo Chứng*).
  * Danh sách phiên đấu giá trực tuyến đang diễn ra, bộ lọc theo danh mục sản phẩm và trạng thái.
* **Chi tiết phiên đấu giá ([auction/detail.html](file:///D:/PTPMH%C4%90T/PROJECT_1/backend/src/main/resources/templates/auction/detail.html)):**
  * Đồng hồ đếm ngược thời gian thực, bảng lịch sử bước giá, biểu đồ giá, nút trả giá trực tiếp (Instant Bid), cài đặt giá trần (Proxy Bid) và nút Mua ngay (Buy It Now).
* **Đăng bán món hàng & Khởi tạo phiên ([catalog/item-form.html](file:///D:/PTPMH%C4%90T/PROJECT_1/backend/src/main/resources/templates/catalog/item-form.html), [auction/create.html](file:///D:/PTPMH%C4%90T/PROJECT_1/backend/src/main/resources/templates/auction/create.html)):**
  * Giao diện dành cho người bán tải ảnh, mô tả tình trạng hàng hóa, cài đặt giá khởi điểm, bước giá, giá sàn ẩn và giá mua ngay.
* **Quản lý Đơn hàng ([order/index.html](file:///D:/PTPMH%C4%90T/PROJECT_1/backend/src/main/resources/templates/order/index.html), [order/detail.html](file:///D:/PTPMH%C4%90T/PROJECT_1/backend/src/main/resources/templates/order/detail.html)):**
  * Theo dõi đơn trúng đấu giá, nộp tiền thanh toán qua ví bảo chứng Escrow, cập nhật trạng thái vận chuyển và đánh giá người bán.
* **Ví tiền nội bộ ([wallet/index.html](file:///D:/PTPMH%C4%90T/PROJECT_1/backend/src/main/resources/templates/wallet/index.html)):**
  * Xem số dư khả dụng, số dư phong tỏa bảo chứng, nạp tiền, rút tiền và tra cứu sổ cái giao dịch bất biến.
* **Cổng Quản trị Admin ([admin/dashboard.html](file:///D:/PTPMH%C4%90T/PROJECT_1/backend/src/main/resources/templates/admin/dashboard.html)):**
  * Thống kê toàn sàn, quản lý danh mục, giám sát các phiên đấu giá và giải quyết khiếu nại.

---

## 📂 THƯ MỤC NGUYÊN MẪU (PROTOTYPE MOCKUP)

Thư mục `frontend/web_app/` lưu trữ bản thiết kế giao diện tĩnh (HTML/CSS mockup) được xây dựng trong giai đoạn phác thảo ý tưởng ban đầu của dự án.
