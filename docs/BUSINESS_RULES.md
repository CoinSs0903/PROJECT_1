# 📜 MA TRẬN QUY TẮC NGHIỆP VỤ & LỊCH TRÌNH BỘ LẬP LỊCH (BUSINESS RULES & SCHEDULER MATRIX)

> **Dự án:** Hệ Thống Đấu Giá Trực Tuyến (Cơ chế Đấu giá Tự động - Proxy Bidding)  
> **Căn cứ:** `docs/SRS.md`, `docs/PLAN.md` (Ngày 2, 4) và `docs/DECISIONS.md`  
> **Mục tiêu:** Định vị chính xác trách nhiệm của từng quy tắc nghiệp vụ trong các module hệ thống, xác định lịch quét tự động ngầm của Scheduler.

---

## 1. MA TRẬN QUY TẮC NGHIỆP VỤ CỐT LÕI (BR-01 ĐẾN BR-25)

| Mã BR | Tóm tắt quy tắc nghiệp vụ | Căn cứ & Quyết định | Module phụ trách | Ngày kế hoạch | File Code / Interface |
| :---: | :--- | :--- | :---: | :---: | :--- |
| **BR-01** | **Tham gia tự do (Instant Bidding):** Bất kỳ người dùng hợp lệ nào cũng có thể đặt giá ngay, không cần đăng ký trước hay nộp cọc. | DECISION #1 | `auction`, `bidding` | Ngày 11, 13 | `ProxyBiddingService` |
| **BR-02** | **Cấm Seller tự đấu giá (Self-bidding):** Người bán không được phép đặt giá trên chính món hàng do mình đăng bán. | Gian lận giá | `proxy_bidding` | Ngày 13 | `ProxyBiddingValidator` |
| **BR-03** | **Bảng bước giá tối thiểu theo bậc (Bid Increment Tiers):**<br>• 1 – 100.000 VNĐ: bước 5.000 VNĐ<br>• 100.000 – 1.000.000 VNĐ: bước 20.000 VNĐ<br>• 1.000.000 – 10.000.000 VNĐ: bước 100.000 VNĐ<br>• Trên 10.000.000 VNĐ: bước 500.000 VNĐ | PLAN Mục 4 #1 | `system`, `common` | Ngày 8, 5 | `SystemConfigService`, `MoneyUtil` |
| **BR-04** | **Giá trần bí mật (Proxy Bidding):** Người mua chỉ gửi `max_amount` lên server. Server bảo mật tuyệt đối, không lộ qua API công khai hay WebSocket. | PLAN Mục 1 #4, DECISION #3 | `proxy_bidding` | Ngày 13 | `ProxyBiddingService`, `ProxyBid` |
| **BR-05** | **Thuật toán nâng giá tự động:** Hệ thống tự động so sánh `max_amount` mới với `max_amount` cao nhất hiện tại để nhảy bước giá thấp nhất đủ để dẫn đầu. | Proxy Bidding Algorithm | `proxy_bidding` | Ngày 13 | `ProxyBiddingEngine` |
| **BR-06** | **Cùng mức giá trần (First-come Priority):** Nếu 2 người đặt cùng mức `max_amount`, người đặt trước sẽ là người dẫn đầu. | Công bằng thời gian | `proxy_bidding` | Ngày 13 | `ProxyBiddingEngine` |
| **BR-07** | **Bảo mật Giá sàn (Reserve Price Privacy):** Người mua chỉ thấy nhãn `Chưa đạt giá sàn` hoặc `Đã đạt giá sàn`. Con số chính xác được ẩn 100% đến khi hết phiên. | PLAN Mục 4 #2, DECISION #4 | `auction` | Ngày 12 | `AuctionDetailDto` |
| **BR-08** | **Đạt ngưỡng Giá sàn (Reserve Met):** Khi có mức giá trần >= `reserve_price`, cờ `is_reserve_met = TRUE` được bật ngay lập tức. | Reserve Price Rule | `proxy_bidding`, `auction` | Ngày 13 | `ProxyBiddingEngine` |
| **BR-09** | **Điều kiện Mua ngay (Buy It Now):** Cho phép người mua mua đứt sản phẩm và kết thúc phiên ngay lập tức với giá `buy_it_now_price`. | Instant Buy | `auction`, `order` | Ngày 15 | `BuyItNowService` |
| **BR-10** | **Ẩn nút Mua ngay:** Nút Buy It Now tự động biến mất vĩnh viễn ngay khi có lượt bid đầu tiên hợp lệ đạt ngưỡng giá sàn. | PLAN Mục 4 #3, DECISION #5 | `auction` | Ngày 15 | `AuctionViewService` |
| **BR-11** | **Thời hạn phiên đấu giá:** Seller lựa chọn thời lượng chuẩn: 3 ngày, 5 ngày hoặc 7 ngày kể từ lúc mở phiên. | Thiết kế phiên | `auction` | Ngày 11 | `AuctionCreateRequest` |
| **BR-12** | **Đóng phiên cứng (Hard-close):** Phiên kết thúc chính xác tại `end_time`, không gia hạn thêm thời gian. Tranh chấp mili-giây cuối giải quyết bằng khóa bi quan `PESSIMISTIC_WRITE`. | PLAN Mục 4 #4, DECISION #6 | `auction`, `proxy_bidding` | Ngày 14, 17 | `AuctionCloserScheduler` |
| **BR-13** | **Điều kiện bán thành công (Sold Condition):** Phiên kết thúc có ít nhất 1 lượt bid và `is_reserve_met = TRUE` (hoặc phiên không cài reserve price). Chuyển sang `ENDED_SOLD`. | Quy tắc đóng phiên | `auction`, `order` | Ngày 14 | `AuctionStateMachine` |
| **BR-14** | **Điều kiện không thành công (Unsold Condition):** Không có lượt bid nào HOẶC giá cao nhất chưa đạt giá sàn. Chuyển sang `ENDED_UNSOLD`. | Quy tắc đóng phiên | `auction` | Ngày 14 | `AuctionStateMachine` |
| **BR-15** | **Khởi tạo đơn hàng (Order Creation):** Khi phiên `ENDED_SOLD` hoặc Buy It Now thành công, hệ thống tự động sinh ra `Order` ở trạng thái `AWAITING_PAYMENT`. | Khớp lệnh đơn hàng | `order` | Ngày 14, 18 | `OrderService`, `AuctionWonListener` |
| **BR-16** | **Thời hạn thanh toán (Payment Deadline):** Người thắng cuộc có tối đa 72 giờ (3 ngày) để thanh toán đơn hàng. | PLAN Mục 4 #5, DECISION #7 | `order`, `system` | Ngày 19, 8 | `OrderPaymentService` |
| **BR-17** | **Bảo chứng thanh toán (Escrow):** Tiền thanh toán của người mua được tạm giữ an toàn trong ví sàn, không chuyển ngay cho người bán cho tới khi giao hàng thành công. | An toàn người mua | `wallet`, `order` | Ngày 19, 7 | `WalletFacade` |
| **BR-18** | **Xử phạt bùng hàng (Unpaid Item Strike):** Người thắng quá 72 giờ không thanh toán sẽ bị hủy đơn (`CANCELLED_UNPAID`) và cộng 1 gậy vi phạm. Tích lũy 3 gậy sẽ bị cấm tham gia đấu giá. | PLAN Mục 4 #6, DECISION #8 | `order`, `user` | Ngày 20, 6 | `OrderTimeoutTask`, `UserService` |
| **BR-19** | **Đăng lại món hàng (Relist):** Seller có quyền tạo phiên đấu giá mới từ chính món hàng (`item_id` tái sử dụng) nếu phiên trước kết thúc `ENDED_UNSOLD` hoặc đơn hàng bị hủy. | PLAN Mục 4 #7, DECISION #10 | `auction` | Ngày 20 | `RelistService` |
| **BR-20** | **Chào bán người về nhì (Second Chance Offer):** Nếu người thắng bùng hàng, Seller được quyền chào bán món hàng cho người về nhì với mức giá cao nhất người đó từng bid. | DECISION #9 | `order`, `auction` | Ngày 20 | `SecondChanceService` |
| **BR-21** | **Rút lại giá hợp lệ (Bid Retraction):** Người mua chỉ được yêu cầu rút giá nếu nhầm lẫn số tiền nghiêm trọng và phải trước khi hết phiên ít nhất 12 giờ. Hệ thống tính toán lại giá hiện tại từ các bidder còn lại. | Hạn chế lạm dụng | `proxy_bidding` | Ngày 18 | `BidRetractionService` |
| **BR-22** | **Phí dịch vụ hoa hồng sàn (Platform Fee):** Sàn thu 5% trên giá trúng đấu giá khi đơn hàng hoàn tất (`COMPLETED`). Tiền phí chuyển vào ví `SYSTEM_REVENUE`. | DECISION #11 | `payout`, `wallet` | Ngày 23, 7 | `PayoutService` |
| **BR-23** | **Giải ngân cho Seller:** Sau khi trừ 5% phí sàn, 95% số tiền còn lại được cộng vào số dư khả dụng (`available_balance`) của Seller. | Hoàn tất giao dịch | `payout`, `wallet` | Ngày 23, 7 | `PayoutService` |
| **BR-24** | **Ẩn danh hiển thị giá (Bidder Privacy):** Lịch sử trả giá công khai che giấu tên người dùng dạng `u***1` để chống thông đồng và bảo mật cá nhân. | Bảo mật người mua | `proxy_bidding`, `common` | Ngày 16, 5 | `StringUtil`, `BidHistoryDto` |
| **BR-25** | **Nhật ký bất biến (Append-only Ledger):** Bảng `transactions`, `bids` và `audit_logs` chỉ thêm mới, tuyệt đối không được sửa hay xóa. | Toàn vẹn kiểm toán | `audit`, `wallet` | Ngày 8, 7 | `TransactionRepository` |

---

## 2. DANH MỤC CÁC TÁC VỤ BỘ LẬP LỊCH TỰ ĐỘNG (`Scheduler Tasks`)

| # | Tên tác vụ Scheduler | Chu kỳ quét | Điều kiện kích hoạt | Hành động nghiệp vụ thực thi | Mã BR |
| :-: | :--- | :---: | :--- | :--- | :---: |
| **1** | **Tự động mở phiên hẹn giờ** (`OpenScheduledAuctionTask`) | Mỗi 10 giây | `CURRENT_TIMESTAMP >= start_time` và `status = 'SCHEDULED'` | Chuyển trạng thái sang `ACTIVE`, phát sự kiện `AuctionStartedEvent`. | BR-11 |
| **2** | **Tự động đóng phiên hết hạn** (`CloseExpiredAuctionTask`) | Mỗi 5 giây | `CURRENT_TIMESTAMP >= end_time` và `status = 'ACTIVE'` | Khóa phiên, kiểm tra điều kiện Reserve Price: nếu hợp lệ chuyển `ENDED_SOLD` và sinh `Order`, ngược lại chuyển `ENDED_UNSOLD`. | BR-12, BR-13, BR-14, BR-15 |
| **3** | **Cảnh báo phiên sắp kết thúc** (`AuctionEndingReminderTask`) | Mỗi 1 phút | `end_time` còn từ 14 đến 15 phút và chưa gửi nhắc nhở | Gửi thông báo `AUCTION_ENDING` cho tất cả bidder đã từng tham gia trả giá. | BR-12 |
| **4** | **Quét đơn hàng quá hạn thanh toán** (`OrderPaymentTimeoutTask`) | Mỗi 1 phút | `CURRENT_TIMESTAMP > payment_deadline` và `status = 'AWAITING_PAYMENT'` | Hủy đơn (`CANCELLED_UNPAID`), phạt 1 gậy vi phạm cho Buyer, thông báo cho Seller tùy chọn Relist hoặc Second Chance Offer. | BR-16, BR-18, BR-19, BR-20 |
| **5** | **Tự động hoàn tất đơn sau giao hàng** (`AutoConfirmDeliveredOrderTask`) | Mỗi 1 giờ | Đơn hàng trạng thái `SHIPPED` quá 7 ngày mà Buyer chưa bấm xác nhận | Tự động chuyển đơn sang `COMPLETED`, kích hoạt giải ngân cho Seller và thu phí sàn. | BR-22, BR-23 |
