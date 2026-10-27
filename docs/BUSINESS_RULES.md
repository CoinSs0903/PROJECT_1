# 📜 MA TRẬN QUY TẮC NGHIỆP VỤ & LỊCH TRÌNH BỘ LẬP LỊCH (BUSINESS RULES & SCHEDULER MATRIX)

> **Dự án:** Hệ Thống Đấu Giá Trực Tuyến (Online Auction System)  
> **Căn cứ:** docs/SRS.md (Mục 4), docs/PLAN.md (Ngày 2)  
> **Mục tiêu:** Định vị chính xác trách nhiệm hiện thực hóa của từng quy tắc vào các module Java Backend, theo dõi tiến độ hoàn thành và lịch chạy của các tác vụ ngầm tự động (Scheduler).

---

## 1. MA TRẬN 35 QUY TẮC NGHIỆP VỤ (BR-01 ĐẾN BR-35)

| Mã BR | Tóm tắt quy tắc nghiệp vụ | Căn cứ pháp lý / Thiết kế | Module phụ trách | Ngày kế hoạch | Trạng thái | File Code hiện thực | File Test kiểm thử |
| :---: | :--- | :--- | :--- | :---: | :---: | :--- | :--- |
| **BR-01** | Mỗi phiên gắn 1 tài sản và 1 phương thức đấu giá (tăng/giảm); quy chế công khai | Điều 34, 40 Luật ĐGTS | `auction` | Ngày 11 | Chưa | | |
| **BR-02** | Tài sản phải hợp pháp; Seller đề xuất giá khởi điểm, Admin phê duyệt | Điều 4, 8 Luật ĐGTS | `auction` | Ngày 11 | Chưa | | |
| **BR-03** | Niêm yết tối thiểu 7 ngày LV (động sản) hoặc 15 ngày (bất động sản) trước khi mở | Điều 35 Luật ĐGTS | `auction`, `system` | Ngày 11, 8 | Chưa | | |
| **BR-04** | Cấm các đối tượng hạn chế/người thân/tổ chức đấu giá đăng ký mua tài sản | Điều 38 khoản 3, 4 | `registration` | Ngày 13 | Chưa | | |
| **BR-05** | Hồ sơ gồm phiếu ĐK & chứng từ; Admin thông báo người không đủ ĐK trước mở phiên 1 ngày LV | Điều 38 khoản 2, 2đ | `registration`, `system` | Ngày 13, 8 | Chưa | | |
| **BR-06** | Cấm Seller đăng ký/trả giá ở phiên của mình; cấm thông đồng, dìm giá, nâng giá | Điều 38 khoản 4, Điều 9 | `registration`, `bidding` | Ngày 13, 16 | Chưa | | |
| **BR-07** | Bidder bắt buộc nộp tiền đặt trước mới được quyền tham gia phòng đấu giá | Điều 39 khoản 1 | `registration`, `wallet` | Ngày 13, 7 | Chưa | | |
| **BR-08** | Mức đặt trước từ 5% đến 20% giá khởi điểm, do Seller đề xuất và Admin duyệt | Điều 39 khoản 1a | `auction`, `registration` | Ngày 11, 13 | Chưa | | |
| **BR-09** | Thời hạn nộp đặt trước: từ ngày niêm yết đến trước ngày mở phiên 2 ngày làm việc | Điều 39 khoản 2 | `registration`, `system` | Ngày 13, 8 | Chưa | | |
| **BR-10** | Tiền đặt trước phong tỏa trong tài khoản ký quỹ riêng biệt (`SYSTEM_ESCROW`), tách khỏi tiền vận hành | Điều 39 khoản 1, Điều 9 | `wallet` | Ngày 7 | Chưa | | |
| **BR-11** | Khi có thay đổi niêm yết: thông báo Bidder; Bidder được chọn tiếp tục hoặc rút lấy lại tiền cọc | Điều 39 khoản 3, 6 | `auction`, `registration` | Ngày 15, 13 | Chưa | | |
| **BR-12** | Hoàn tiền đặt trước trong 3 ngày LV sau kết thúc phiên cho người không trúng / từ chối thay đổi niêm yết | Điều 39 khoản 4 | `payout`, `system` | Ngày 23, 8 | Chưa | | |
| **BR-13** | Tiền đặt trước người trúng chuyển thành tiền cọc; Admin chuyển cho Seller trong 3 ngày LV | Điều 39 khoản 5 | `payout`, `system` | Ngày 23, 8 | Chưa | | |
| **BR-14** | Mất tiền đặt trước nếu: vắng mặt không lý do, bị truất quyền, rút giá, hủy/không trả tiền trúng (chuyển Seller) | Điều 39 khoản 6, 7, 8 | `payout`, `bidding`, `settlement` | Ngày 23, 18, 19 | Chưa | | |
| **BR-15** | Đấu giá tăng dần: bước giá bắt buộc, lần đầu >= giá khởi điểm, sau >= giá hiện tại + bước giá; theo giờ server | Điều 40; Thiết kế | `bidding`, `common` | Ngày 16, 5 | Chưa | | |
| **BR-16** | Hết giờ tăng dần người trả giá cao nhất là người trúng; tuyệt đối không gia hạn thêm giờ | Điều 41; Thiết kế | `bidding` | Ngày 16 | Chưa | | |
| **BR-17** | Người dẫn đầu rút lại giá trước công bố: phiên tiếp tục từ giá liền kề; người rút mất cọc & truất quyền | Điều 50 khoản 1, 4 | `bidding`, `wallet` | Ngày 17, 7 | Chưa | | |
| **BR-18** | Người trúng từ chối: xét người liền kề nếu (giá liền kề + cọc) >= giá trúng và người đó đồng ý mua | Điều 51 khoản 1 | `settlement` | Ngày 19 | Chưa | | |
| **BR-19** | Đấu giá giảm dần: giá khởi điểm cao nhất, tự giảm theo bước giảm và chu kỳ thời gian đến giá sàn | Điều 40 khoản 2; Thiết kế | `bidding`, `common` | Ngày 17, 5 | Chưa | | |
| **BR-20** | Người đầu tiên bấm chấp nhận giá (theo giờ server) là người trúng; dừng giảm giá ngay | Điều 40 khoản 2 | `bidding` | Ngày 17 | Chưa | | |
| **BR-21** | Đấu giá giảm dần chạm giá sàn mà không ai chấp nhận mua thì phiên không thành | Điều 52 khoản 1 | `bidding` | Ngày 17 | Chưa | | |
| **BR-22** | Rút lại / từ chối ở giảm dần: phiên tiếp tục ngay từ giá của người đó và giá tiếp tục giảm | Điều 50 k3, Điều 51 k2 | `bidding`, `settlement` | Ngày 17, 19 | Chưa | | |
| **BR-23** | Chỉ có 1 người đăng ký/tham gia/trả giá thì vẫn bán nếu hoàn tất thủ tục và không còn khiếu nại | Điều 49 Luật ĐGTS | `bidding`, `settlement`, `complaint` | Ngày 16, 18, 24 | Chưa | | |
| **BR-24** | Các trường hợp đấu giá không thành (không ai ĐK, không ai dự, không ai trả giá, hết người kế tiếp) | Điều 52 khoản 1; Thiết kế | `auction`, `bidding`, `settlement` | Ngày 15, 17, 19 | Chưa | | |
| **BR-25** | Công bố người trúng và chuyển sang "Chờ thanh toán", hiển thị công khai mã ẩn danh real-time | Thiết kế | `settlement`, `realtime` | Ngày 18, 21 | Chưa | | |
| **BR-26** | Thanh toán phần còn lại trong hạn ngắn (mặc định 15 phút); thanh toán đủ chuyển "Hoàn tất" | Thiết kế | `settlement`, `wallet` | Ngày 18, 7 | Chưa | | |
| **BR-27** | Người trúng hủy/quá hạn thanh toán: bị mất cọc, công khai cập nhật, áp dụng BR-18 hoặc BR-22 | Điều 51; Thiết kế | `settlement` | Ngày 19 | Chưa | | |
| **BR-28** | Mốc kết thúc phiên để tính hạn 3 ngày LV (hoàn tiền, chuyển cọc) là lúc chuyển "Hoàn tất" hoặc "Không thành" | Điều 39; Thiết kế | `payout`, `system` | Ngày 23, 8 | Chưa | | |
| **BR-29** | Phí dịch vụ sàn bằng 5% giá trúng (Admin cấu hình), trừ vào số tiền Seller nhận | Điều 66; Thiết kế | `payout` | Ngày 23 | Chưa | | |
| **BR-30** | Chuyển cọc cho Seller trong 3 ngày LV; chuyển phần còn lại (trừ phí sàn) khi Bidder xác nhận nhận tài sản | Điều 39 khoản 5; Thiết kế | `payout` | Ngày 23 | Chưa | | |
| **BR-31** | Hiển thị mã ẩn danh (dãy số) cho mọi Bidder; chỉ Admin thấy danh tính; thông tin Seller công khai | Điều 9 khoản 1, 2 | `realtime`, `registration` | Ngày 21, 13 | Chưa | | |
| **BR-32** | Nhật ký chỉ ghi thêm (append-only) không sửa/xóa; tự động lập biên bản điện tử khi phiên kết thúc | Điều 44, 54 Luật ĐGTS | `audit` | Ngày 8, 25 | Chưa | | |
| **BR-33** | Kết quả phiên công khai và gửi thông báo trong ứng dụng (`notifications`) cho các bên tham gia | Điều 43b khoản 5; Thiết kế | `notification` | Ngày 9 | Chưa | | |
| **BR-34** | Seller hủy trước giờ mở không phạt; Admin hủy do vi phạm; hoàn đủ tiền cọc cho Bidder không vi phạm | Thiết kế | `auction`, `payout` | Ngày 15, 23 | Chưa | | |
| **BR-35** | Quyền khiếu nại của Bidder/Seller; Admin xử lý và phản hồi; chưa xử lý xong thì không bán cho 1 người | Điều 49 k2, Điều 74, 75 | `complaint` | Ngày 24 | Chưa | | |

---

## 2. DANH MỤC CÁC TÁC VỤ BỘ LẬP LỊCH TỰ ĐỘNG (`Scheduler Tasks`)

Hệ thống sử dụng Spring Task Scheduling (`@Scheduled`) kết hợp Quartz/Database Clock để đảm bảo xử lý chính xác theo thời gian thực:

| # | Tên tác vụ Scheduler | Chu kỳ quét / Trigger | Điều kiện kích hoạt | Hành động nghiệp vụ thực thi | Mã BR liên quan |
| :-: | :--- | :--- | :--- | :--- | :--- |
| **1** | **Đóng cổng đăng ký tham gia** (`CloseRegistrationTask`) | Quét mỗi 1 phút | `CURRENT_TIMESTAMP >= registration_deadline` (trước mở 2 ngày LV) và `status = 'LISTED'` | Chuyển `auctions.status` $\rightarrow$ `WAITING_FOR_OPENING`. Nếu số hồ sơ hợp lệ = 0 $\rightarrow$ chuyển thẳng sang `FAILED`. | BR-09, BR-24 |
| **2** | **Khai mạc phiên đấu giá** (`OpenAuctionTask`) | Quét mỗi 1 giây (hoặc hẹn giờ chính xác) | `CURRENT_TIMESTAMP >= start_time` và `status = 'WAITING_FOR_OPENING'` | Chuyển `auctions.status` $\rightarrow$ `IN_PROGRESS`, phát thông báo WebSocket mở phòng đấu giá trực tuyến. | BR-15, BR-19, BR-31 |
| **3** | **Tự động giảm giá định kỳ** (`PriceReductionTask`) | Quét mỗi 1 giây theo `price_reduction_interval_seconds` | Phiên giảm dần (`DESCENDING`), `status = 'IN_PROGRESS'` và chưa có người chấp nhận | Tính toán: `current_price = current_price - step_price`. Nếu `current_price <= floor_price` $\rightarrow$ giữ tại sàn 1 chu kỳ, nếu vẫn không ai mua $\rightarrow$ chuyển `FAILED`. Broadcast giá mới qua WebSocket. | BR-19, BR-20, BR-21 |
| **4** | **Hết giờ đấu giá tăng dần** (`EndAuctionTimerTask`) | Quét mỗi 1 giây | Phiên tăng dần (`ASCENDING`), `status = 'IN_PROGRESS'` và `CURRENT_TIMESTAMP >= end_time` | Đóng nhận giá. Nếu có người trả giá $\rightarrow$ chuyển `WAITING_FOR_PAYMENT`, tạo bản ghi `settlements`. Nếu không ai trả giá $\rightarrow$ chuyển `FAILED`. | BR-16, BR-24, BR-25 |
| **5** | **Quá hạn thanh toán người trúng** (`PaymentTimeoutTask`) | Quét mỗi 10 giây | `status = 'WAITING_FOR_PAYMENT'` và `CURRENT_TIMESTAMP > payment_deadline` (sau 15 phút) | Đánh dấu `settlements.status = 'EXPIRED'`. Phạt cọc (`deposits.status = 'FORFEITED'`). Kích hoạt chuyển người liền kề (BR-18) hoặc tiếp tục giảm dần (BR-22). | BR-26, BR-27, BR-18, BR-22 |
| **6** | **Tự động hoàn tiền đặt trước** (`AutoRefundDepositTask`) | Chạy định kỳ đầu giờ sáng (ví dụ 08:00 hàng ngày) | `deposits.status = 'SUBMITTED'` và phiên đã kết thúc (`COMPLETED` hoặc `FAILED`) đủ 3 ngày làm việc | Mở khóa ví ký quỹ, cộng tiền vào ví khả dụng của Bidder không trúng/từ chối hợp lệ. Cập nhật `deposits.status = 'REFUNDED'`, ghi `transactions` và `audit_logs`. | BR-12, BR-28 |
| **7** | **Tự động chuyển cọc cho Seller** (`AutoTransferDepositTask`) | Chạy định kỳ đầu giờ sáng (ví dụ 08:30 hàng ngày) | `deposits.status = 'CONVERTED_TO_DEPOSIT'` hoặc `'FORFEITED'` và phiên đã kết thúc đủ 3 ngày làm việc | Chuyển tiền từ ví ký quỹ sang ví của Seller. Cập nhật `deposits.status = 'TRANSFERRED_TO_SELLER'`, ghi `transactions` và `audit_logs`. | BR-13, BR-14, BR-30 |

---

## 3. NGUYÊN TẮC THIẾT KẾ BỘ LẬP LỊCH TRÁNH RACE CONDITION

1. **Idempotency (Tính bất biến kép):** Mọi tác vụ Scheduler khi quét phải lọc chính xác trạng thái nguồn (ví dụ: `WHERE status = 'WAITING_FOR_PAYMENT' AND payment_deadline < NOW()`) và thực thi trong Transaction độc lập.
2. **Distributed Lock / Database Lock:** Trong môi trường triển khai thực tế hoặc kiểm thử nhiều luồng, câu truy vấn cập nhật phải sử dụng `SELECT ... FOR UPDATE` hoặc `@Lock(LockModeType.PESSIMISTIC_WRITE)` để chống việc hai luồng scheduler cùng can thiệp vào một phiên đấu giá.
