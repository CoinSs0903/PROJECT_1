# 🔄 TÀI LIỆU SƠ ĐỒ CHUYỂN TRẠNG THÁI (STATE MACHINES)

> **Dự án:** Hệ Thống Đấu Giá Trực Tuyến (Cơ chế Đấu giá Tự động)  
> **Căn cứ:** `docs/SRS.md`, `docs/ERD.md`, `docs/PLAN.md` (Ngày 2)  
> **Nguyên tắc cốt lõi:** Duy nhất các State Machine và Service chuyên biệt được chuyển đổi trạng thái thực thể; mọi biến động phải tuân theo luồng hợp lệ.

---

## 1. MÁY TRẠNG THÁI PHIÊN ĐẤU GIÁ (`AuctionStateMachine`)

Vòng đời của một phiên đấu giá trong bảng `auctions` gồm các trạng thái:
- `DRAFT`: Bản nháp chưa công bố.
- `SCHEDULED`: Đã lên lịch, chờ đến giờ `start_time`.
- `ACTIVE`: Đang mở cho người dùng trả giá (phân nhánh logic `is_reserve_met = false/true`).
- `ENDED_SOLD`: Kết thúc thành công (đã có người thắng thỏa mãn điều kiện giá).
- `ENDED_UNSOLD`: Kết thúc không thành công (không có ai bid hoặc không đạt giá sàn).
- `CANCELED`: Phiên bị người bán hủy (khi chưa có lượt bid nào) hoặc bị Admin cưỡng chế hủy.

### 1.1. Sơ đồ Mermaid `AuctionStateMachine`

```mermaid
stateDiagram-v2
    [*] --> DRAFT: Seller khởi tạo phiên
    DRAFT --> SCHEDULED: Hẹn giờ mở phiên trong tương lai
    DRAFT --> ACTIVE: Mở phiên ngay lập tức (start_time <= now)
    
    SCHEDULED --> ACTIVE: Scheduler quét đến giờ start_time
    SCHEDULED --> CANCELED: Seller hủy trước khi phiên bắt đầu
    
    state ACTIVE {
        [*] --> RESERVE_NOT_MET: Khởi đầu (hoặc phiên có Reserve Price)
        RESERVE_NOT_MET --> RESERVE_MET: Có lượt bid/max_amount >= reserve_price
        RESERVE_MET --> RESERVE_MET: Tiếp tục tăng giá tự động
        RESERVE_NOT_MET --> RESERVE_NOT_MET: Tiếp tục trả giá dưới giá sàn
    }
    
    ACTIVE --> ENDED_SOLD: Người mua chọn Buy It Now (Mua ngay)
    ACTIVE --> ENDED_SOLD: Hết giờ (end_time) & có lượt bid & đạt Reserve Price
    ACTIVE --> ENDED_UNSOLD: Hết giờ (end_time) & (không có bid HOẶC chưa đạt Reserve Price)
    ACTIVE --> CANCELED: Seller hủy phiên (chỉ được khi bid_count = 0) / Admin hủy vi phạm
    
    ENDED_UNSOLD --> ACTIVE: Seller sử dụng tính năng Relist (tạo phiên mới từ item_id cũ)
    
    ENDED_SOLD --> [*]
    CANCELED --> [*]
```

---

## 2. MÁY TRẠNG THÁI ĐƠN HÀNG (`OrderStateMachine`)

Vòng đời đơn hàng trong bảng `orders` sau khi chốt người thắng phiên đấu giá hoặc Buy It Now:
- `AWAITING_PAYMENT`: Chờ người mua thanh toán tiền hàng (hạn 72 giờ).
- `PAID`: Đã thanh toán tiền hàng qua Ví (tiền được tạm giữ an toàn trong ví Escrow).
- `SHIPPED`: Người bán đã bàn giao hàng cho đơn vị vận chuyển và cập nhật mã vận đơn (`tracking_number`).
- `DELIVERED`: Người mua đã nhận được hàng hoặc hệ thống tự động xác nhận sau 7 ngày.
- `COMPLETED`: Đơn hàng hoàn tất, giải ngân tiền cho Seller (trừ phí sàn 5%).
- `CANCELLED_UNPAID`: Quá hạn 72 giờ không thanh toán, người mua bị phạt 1 gậy (Unpaid Item Strike).
- `DISPUTED`: Đơn hàng bị khiếu nại (Item not received / Item not as described).
- `REFUNDED`: Giải quyết khiếu nại chấp thuận hoàn tiền cho Buyer.

### 2.1. Sơ đồ Mermaid `OrderStateMachine`

```mermaid
stateDiagram-v2
    [*] --> AWAITING_PAYMENT: Tự động tạo khi phiên ENDED_SOLD hoặc Buy It Now
    
    AWAITING_PAYMENT --> PAID: Buyer thanh toán thành công qua số dư Ví
    AWAITING_PAYMENT --> CANCELLED_UNPAID: Quá hạn 72 giờ (phạt 1 gậy bùng hàng)
    
    PAID --> SHIPPED: Seller cập nhật mã vận đơn (tracking_number)
    SHIPPED --> DELIVERED: Buyer xác nhận đã nhận hàng / Auto-delivered sau 7 ngày
    
    DELIVERED --> COMPLETED: Không có khiếu nại sau 3 ngày -> Giải ngân cho Seller (trừ 5% phí)
    
    SHIPPED --> DISPUTED: Buyer mở khiếu nại hàng lỗi / không nhận được hàng
    DELIVERED --> DISPUTED: Buyer mở khiếu nại trong hạn 3 ngày
    
    DISPUTED --> COMPLETED: Admin phán quyết Seller thắng -> Giải ngân
    DISPUTED --> REFUNDED: Admin phán quyết Buyer thắng -> Hoàn tiền ví Buyer
    
    CANCELLED_UNPAID --> [*]
    COMPLETED --> [*]
    REFUNDED --> [*]
```

---

## 3. MÁY TRẠNG THÁI GIAO DỊCH VÍ (`WalletTransactionStateMachine`)

Bảng `transactions` là nhật ký bất biến (Append-only) ghi nhận biến động số dư ví:
- `TOPUP`: Nạp tiền vào ví người dùng (tăng `available_balance`).
- `WITHDRAW`: Rút tiền từ ví về tài khoản ngân hàng (giảm `available_balance`).
- `PAYMENT`: Thanh toán tiền đơn hàng trúng đấu giá (giảm ví Buyer, chuyển tiền vào ví bảo chứng).
- `PAYOUT`: Giải ngân tiền bán hàng (cộng vào ví Seller sau khi trừ phí).
- `SERVICE_FEE`: Thu phí hoa hồng sàn 5% (cộng vào ví `SYSTEM_REVENUE`).
- `REFUND`: Hoàn tiền đơn hàng bị hủy hoặc khiếu nại (cộng lại ví Buyer).

---

## 4. MÁY TRẠNG THÁI KHIẾU NẠI (`ComplaintStateMachine`)

Vòng đời khiếu nại trong bảng `complaints`:
- `PENDING`: Người mua gửi khiếu nại kèm chứng cứ hình ảnh.
- `UNDER_REVIEW`: Admin đang tiếp nhận và đối chất 2 bên.
- `RESOLVED`: Khiếu nại được giải quyết (hoàn tiền hoặc giải ngân).
- `REJECTED`: Admin bác bỏ khiếu nại do không đủ chứng cứ.

```mermaid
stateDiagram-v2
    [*] --> PENDING: Buyer gửi đơn khiếu nại
    PENDING --> UNDER_REVIEW: Admin bắt đầu thụ lý hồ sơ
    UNDER_REVIEW --> RESOLVED: Admin đưa ra quyết định xử lý
    UNDER_REVIEW --> REJECTED: Khiếu nại vô căn cứ
    RESOLVED --> [*]
    REJECTED --> [*]
```

---

## 5. MÁY TRẠNG THÁI NGƯỜI DÙNG (`UserStatusStateMachine`)

Vòng đời trạng thái người dùng trong bảng `users`:
- `ACTIVE`: Tài khoản hoạt động bình thường, được quyền mua và bán.
- `RESTRICTED_BIDDING`: Tích lũy 3 gậy bùng hàng (`unpaid_strikes_count >= 3`), bị cấm tham gia đặt giá trong 30 ngày.
- `BANNED`: Vi phạm nghiêm trọng gian lận hoặc lừa đảo, bị Admin khóa vĩnh viễn.

```mermaid
stateDiagram-v2
    [*] --> ACTIVE: Đăng ký tài khoản thành công
    ACTIVE --> RESTRICTED_BIDDING: Bùng hàng đủ 3 lần (3 Unpaid Item Strikes)
    RESTRICTED_BIDDING --> ACTIVE: Hết thời hạn kỷ luật 30 ngày / Admin gỡ gậy
    ACTIVE --> BANNED: Admin cưỡng chế khóa do gian lận
    RESTRICTED_BIDDING --> BANNED: Tiếp tục vi phạm chính sách sàn
    BANNED --> [*]
```
