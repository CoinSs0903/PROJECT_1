# 📜 BẢN GHI CÁC QUYẾT ĐỊNH THIẾT KẾ (DECISIONS LOG)

> **Dự án:** Hệ Thống Đấu Giá Trực Tuyến (Online Auction System)  
> **Mục đích:** Ghi nhận toàn bộ các quyết định kiến trúc, giả định nghiệp vụ và điểm đã chốt khi SRS chưa nêu rõ hoặc có nhiều cách hiểu.

---

## 1. CÁC QUYẾT ĐỊNH NGHIỆP VỤ ĐÃ CHỐT

| # | Vấn Đề Nghiệp Vụ | Quyết Định Áp Dụng | Căn Cứ & Lý Do |
| :---: | :--- | :--- | :--- |
| **1** | **Thời hạn thanh toán sau khi trúng đấu giá** | Mặc định **15 phút** kể từ thời điểm công bố người trúng; Admin có thể cấu hình lại trong `system_configs`. | SRS 9.3 mục 1. Phù hợp tính chất trực tuyến thời gian thực. |
| **2** | **Thời gian niêm yết bất động sản** | Tính theo **15 ngày thường** (calendar days), trong khi động sản tính theo **7 ngày làm việc**. | BR-03 và Điều 35 Luật Đấu giá tài sản. |
| **3** | **Người đang dẫn đầu có được trả giá tiếp không** | **Không cho phép**. Người đang dẫn đầu không cần và không được tự đẩy giá của chính mình lên. | BR-15. Chống thao túng giá hoặc thao tác nhầm. |
| **4** | **Xử lý khi giá chạm sàn ở đấu giá giảm dần** | Giữ tại mức giá sàn thêm **đúng một chu kỳ giảm** (hoặc đến khi hết giờ), nếu vẫn không ai chấp nhận thì phiên mới chuyển sang **Không thành**. | BR-21. Tạo cơ hội cuối cùng cho người mua. |
| **5** | **Rút lại giá ở đấu giá giảm dần** | Không có nút rút lại riêng ở giảm dần; việc hủy ở bước "Chờ thanh toán" được xử lý theo quy định mất cọc BR-27 và tiếp tục phiên BR-22. | BR-20, BR-25 (hệ thống chốt và công bố ngay khi có người bấm chấp nhận). |
| **6** | **Cơ chế công bố người trúng đấu giá** | **Hệ thống tự động xác định và công bố ngay lập tức** khi hết giờ (tăng dần) hoặc có người bấm chấp nhận (giảm dần). Admin chỉ giám sát và can thiệp khi có vi phạm. | BR-25. Đảm bảo tính minh bạch và tức thì. |
| **7** | **Ai được quyền rút lại giá ở đấu giá tăng dần** | **Chỉ người đang trả giá cao nhất (dẫn đầu)** mới được rút lại; khi rút thì bị truất quyền tham dự và mất tiền đặt trước. | BR-17, Điều 50 khoản 1. |
| **8** | **Thời hạn trả lời của người trả giá liền kề (BR-18)** | Dùng cùng thời hạn thanh toán được cấu hình (mặc định 15 phút). Người liền kề xác nhận đồng ý mua bằng hành động thanh toán. | BR-18, BR-26. |
| **9** | **Trạng thái "Tạm dừng phiên"** | **Không áp dụng trạng thái tạm dừng**; chỉ giữ cơ chế Hủy phiên và Truất quyền người vi phạm để tránh làm sai lệch giờ đếm ngược real-time. | BR-16 (quy định không gia hạn thời gian). |
| **10** | **Thời hạn gửi và giải quyết khiếu nại** | Người tham gia được gửi khiếu nại trong vòng **30 ngày** kể từ khi phiên kết thúc; Admin có trách nhiệm phản hồi và xử lý trong vòng **7 ngày**. | SRS 9.3 mục 4. |
| **11** | **Quy tắc làm tròn phí dịch vụ** | Làm tròn **xuống** đến đơn vị đồng (`FLOOR`). | BR-29. Tránh thu vượt quá tỷ lệ quy định. |
| **12** | **Thời điểm hoàn tiền khi phiên bị hủy** | **Hoàn ngay lập tức** vào ví khả dụng của các Bidder không vi phạm (vẫn đáp ứng quy định "trong vòng 3 ngày làm việc"). | BR-34. Tối ưu trải nghiệm người dùng. |
| **13** | **Hồ sơ Seller bị từ chối có được gửi lại không** | **Cho phép gửi lại** sau khi người dùng cập nhật, bổ sung theo lý do từ chối của Admin. | FR-SE-01. |

---

## 2. CÁC QUYẾT ĐỊNH KỸ THUẬT & CƠ SỞ DỮ LIỆU

1. **Đơn vị tiền tệ:** Lưu kiểu `BIGINT` đơn vị Việt Nam Đồng (VND), không dùng số thập phân (float/double) để triệt tiêu lỗi làm tròn tiền.
2. **Đồng bộ thời gian:** `DATETIME(3)` với múi giờ máy chủ cố định `Asia/Ho_Chi_Minh`. Tất cả tính toán hết giờ, giảm giá, trả giá đều quy về Server Clock.
3. **Ví hệ thống (System Wallets):** Tạo 2 ví đặc thù trong bảng `wallets` với `user_id = NULL`:
   - `SYSTEM_ESCROW`: Ví giữ tiền ký quỹ và tiền cọc trung gian.
   - `SYSTEM_REVENUE`: Ví thu phí dịch vụ đấu giá của hệ thống.
4. **An toàn giao dịch tiền tệ:** Mỗi thao tác biến động ví bắt buộc có một bản ghi `transactions` đi kèm với `idempotency_key` để chống gọi trùng lặp (Double-Spending).
