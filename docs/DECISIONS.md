# BẢN GHI CÁC QUYẾT ĐỊNH THIẾT KẾ (DECISIONS LOG)

> **Dự án:** Hệ Thống Đấu Giá Trực Tuyến (Online Auction System)  
> **Căn cứ:** `docs/PLAN.md` (Ngày 1) và `docs/ERD.md`  
> **Mục đích:** Ghi nhận các quyết định kiến trúc, giả định nghiệp vụ cốt lõi khi áp dụng cơ chế đấu giá tự động (Proxy Bidding).

---

## 1. CÁC QUYẾT ĐỊNH NGHIỆP VỤ ĐÃ CHỐT

| # | Vấn đề nghiệp vụ | Quyết định áp dụng | Căn cứ & Lý do |
| :---: | :--- | :--- | :--- |
| **1** | **Rào cản tham gia (Instant Bidding)** | **Không yêu cầu đăng ký trước và không thu tiền đặt cọc.** Bất kỳ tài khoản người dùng hợp lệ nào cũng có thể vào đặt giá ngay. | Tối đa hóa tỷ lệ chuyển đổi và tính thanh khoản cho phiên đấu giá. |
| **2** | **Cơ chế Đấu giá tự động (Proxy Bidding)** | **Người dùng nhập giá trần bí mật (Max Bid).** Hệ thống tự động nâng giá của người dẫn đầu theo bước giá tối thiểu đủ để vượt qua đối thủ. | Tránh việc người mua phải canh màn hình 24/7. |
| **3** | **Bảo mật Giá trần (Max Bid Privacy)** | **Bí mật tuyệt đối ở phía Server.** Không bao giờ gửi giá trị `max_amount` qua API công khai hay WebSocket. | Chống lộ bài để bidder khác không thể dò giá. |
| **4** | **Quy tắc Giá sàn ẩn (Reserve Price)** | Người mua chỉ thấy trạng thái: **"Chưa đạt giá sàn"** hoặc **"Đã đạt giá sàn"**, không bao giờ thấy số tiền cụ thể cho đến khi kết thúc phiên. | Bảo vệ mức giá tối thiểu mà người bán kỳ vọng mà không làm mất tính hấp dẫn của giá khởi điểm thấp. |
| **5** | **Thời điểm nút Mua ngay (Buy It Now) biến mất** | Nút Buy It Now sẽ **tự động biến mất ngay khi có lượt bid đầu tiên hợp lệ đạt ngưỡng giá sàn**. | Đảm bảo khi đấu giá đã bước vào vùng bán được, tùy chọn mua đứt sẽ kết thúc để bảo đảm cạnh tranh công bằng. |
| **6** | **Quy tắc kết thúc phiên (Sniping vs Extension)** | Áp dụng **Hard-close**: Phiên kết thúc chính xác tại thời điểm `end_time` đã định trước, không gia hạn thêm thời gian. Tranh chấp giây cuối được bảo vệ bằng Khóa bi quan Database (`SELECT ... FOR UPDATE`). | Giải quyết xung đột giây cuối bằng kỹ thuật Concurrency Control. |
| **7** | **Thời hạn thanh toán đơn hàng (Payment Deadline)** | Người thắng cuộc có tối đa **72 giờ (3 ngày)** để tiến hành thanh toán đơn hàng trúng đấu giá. | Cho phép người mua có thời gian chuẩn bị tiền và xác nhận địa chỉ giao hàng. |
| **8** | **Xử lý người thắng bùng hàng (Non-paying Bidder)** | Đơn hàng quá 72 giờ không thanh toán sẽ chuyển sang `CANCELLED_UNPAID`. Người mua bị cộng **1 gậy vi phạm (Unpaid Item Strike)**. Nếu tích lũy đủ 3 gậy sẽ bị cấm tham gia đấu giá. | Cơ chế răn đe thay thế cho việc tịch thu tiền đặt cọc. |
| **9** | **Quyền lợi của Người bán khi bị bùng hàng** | Người bán được quyền chọn 1 trong 2 giải pháp: (1) **Relist** (tạo phiên mới từ món hàng đó); hoặc (2) **Second Chance Offer** (chào bán cho người về nhì với mức giá người đó từng bid). | Đảm bảo người bán luôn có phương án xử lý hàng hóa nhanh nhất. |
| **10** | **Đăng lại món hàng (Relist Capability)** | Bảng `auctions` không đặt khóa `UNIQUE(item_id)` để 1 món hàng (`Item`) có thể trải qua nhiều phiên đấu giá khác nhau nếu các phiên trước không thành công. | Cho phép tái sử dụng thông tin món hàng và hình ảnh mà không cần tạo lại từ đầu. |
| **11** | **Khấu trừ phí hoa hồng sàn (Platform Service Fee)** | Sàn thu **5% trên giá trúng đấu giá** của món hàng. Phí này chỉ được trừ khi người mua xác nhận đã nhận hàng thành công và giải ngân vào ví người bán. | Đảm bảo tính công bằng: sàn chỉ thu phí khi giao dịch thành công trọn vẹn. |

---

## 2. CÁC QUYẾT ĐỊNH KỸ THUẬT & CƠ SỞ DỮ LIỆU

1. **Chuẩn hóa 14 bảng dữ liệu:**
   - Người dùng & Ví: `users`, `wallets`, `transactions`.
   - Danh mục & Hàng hóa: `categories`, `items`, `item_images`.
   - Đấu giá: `auctions`, `proxy_bids`, `bids`.
   - Đơn hàng & Đánh giá: `orders`, `reviews`.
   - Vận hành: `notifications`, `complaints`, `system_configs`.
2. **Loại bỏ hoàn toàn các bảng cọc và thủ tục hành chính:**
   - Xóa bỏ: `registrations`, `deposits`, `holidays`, `auction_minutes`, `listing_change_requests`, `seller_profiles`.
3. **Tiền tệ & Toàn vẹn:** Kiểu `BIGINT` đơn vị VNĐ, `CHECK (available_balance >= 0)`.
4. **Idempotency:** Cột `idempotency_key UNIQUE` trong `transactions` chống trừ tiền hoặc cộng tiền 2 lần khi mạng retry.
