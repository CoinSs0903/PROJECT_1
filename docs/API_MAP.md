# 🗺️ BẢN ĐỒ API & WEBSOCKET CHANNELS (API MAP)

> **Dự án:** Hệ Thống Đấu Giá Trực Tuyến (Cơ chế Đấu giá Tự động)  
> **Căn cứ:** `docs/PLAN.md` (Ngày 4)

---

## 1. GIAO DIỆN WEB (THYMELEAF CONTROLLER ENDPOINTS)

| Phương thức | Đường dẫn URL | Mô tả màn hình hiển thị | Phân quyền |
| :---: | :--- | :--- | :---: |
| `GET` | `/` | Trang chủ: Tìm kiếm, phiên nổi bật, phiên sắp kết thúc | Public |
| `GET` | `/login` | Màn hình đăng nhập | Public |
| `GET` | `/register` | Màn hình đăng ký tài khoản | Public |
| `GET` | `/profile` | Xem và cập nhật hồ sơ cá nhân | `ROLE_USER`, `ROLE_ADMIN` |
| `GET` | `/wallet` | Quản lý ví tiền, xem số dư và lịch sử giao dịch | `ROLE_USER` |
| `GET` | `/items` | Danh sách món hàng đã đăng của Seller | `ROLE_USER` |
| `GET` | `/items/new` | Màn hình thêm mới món hàng & ảnh | `ROLE_USER` |
| `GET` | `/items/{id}/edit`| Chỉnh sửa món hàng | `ROLE_USER` |
| `GET` | `/auctions/create` | Màn hình tạo phiên đấu giá từ món hàng | `ROLE_USER` |
| `GET` | `/auctions/{id}` | Chi tiết phòng đấu giá trực tuyến (Live room) | Public |
| `GET` | `/orders` | Danh sách đơn hàng đã mua và đã bán | `ROLE_USER` |
| `GET` | `/orders/{id}` | Chi tiết đơn hàng và màn hình thanh toán | `ROLE_USER` |
| `GET` | `/notifications` | Danh sách tất cả thông báo của người dùng | `ROLE_USER` |
| `GET` | `/admin` | Bảng điều khiển quản trị hệ thống | `ROLE_ADMIN` |

---

## 2. DỊCH VỤ REST API (`/api/*`)

### 2.1. Xác thực & Tài khoản (`user`)
| Method | Endpoint | Mô tả | Quyền |
| :---: | :--- | :--- | :---: |
| `POST` | `/api/auth/register` | Đăng ký tài khoản mới | Public |
| `GET` | `/api/user/me` | Lấy thông tin tài khoản hiện tại | User |
| `PUT` | `/api/user/profile` | Cập nhật địa chỉ nhận hàng & ngân hàng | User |

### 2.2. Quản lý Ví (`wallet`)
| Method | Endpoint | Mô tả | Quyền |
| :---: | :--- | :--- | :---: |
| `GET` | `/api/wallet/balance` | Lấy số dư khả dụng hiện tại | User |
| `POST` | `/api/wallet/topup` | Nạp tiền vào ví | User |
| `POST` | `/api/wallet/withdraw` | Rút tiền từ ví về ngân hàng | User |
| `GET` | `/api/wallet/transactions` | Lịch sử giao dịch ví | User |

### 2.3. Danh mục & Món hàng (`catalog`)
| Method | Endpoint | Mô tả | Quyền |
| :---: | :--- | :--- | :---: |
| `GET` | `/api/categories` | Danh sách cây danh mục sản phẩm | Public |
| `GET` | `/api/items` | Danh sách món hàng của Seller | User |
| `POST` | `/api/items` | Tạo mới món hàng | User |
| `GET` | `/api/items/{id}` | Xem chi tiết món hàng | User |

### 2.4. Phiên Đấu giá & Đặt giá (`auction`, `proxy_bidding`)
| Method | Endpoint | Mô tả | Quyền |
| :---: | :--- | :--- | :---: |
| `GET` | `/api/auctions` | Tìm kiếm và lọc danh sách phiên đấu giá | Public |
| `GET` | `/api/auctions/{id}` | Xem chi tiết phiên (ẩn danh sách max bid) | Public |
| `POST` | `/api/auctions` | Tạo phiên đấu giá mới | User |
| `POST` | `/api/auctions/{id}/bids` | Đặt mức giá trần tự động (`max_amount`) | User |
| `POST` | `/api/auctions/{id}/buy-it-now` | Mua ngay sản phẩm | User |
| `POST` | `/api/auctions/{id}/retract-bid` | Yêu cầu rút lại giá hợp lệ | User |
| `POST` | `/api/auctions/relist/{itemId}` | Đăng lại phiên mới từ món hàng cũ | User |

### 2.5. Đơn hàng (`order`)
| Method | Endpoint | Mô tả | Quyền |
| :---: | :--- | :--- | :---: |
| `GET` | `/api/orders/{id}` | Chi tiết đơn hàng | User |
| `POST` | `/api/orders/{id}/pay` | Thanh toán đơn hàng qua Ví | User |
| `POST` | `/api/orders/{id}/ship` | Seller cập nhật mã vận đơn giao hàng | User |
| `POST` | `/api/orders/{id}/confirm` | Buyer xác nhận đã nhận hàng | User |

### 2.6. Thông báo (`notification`)
| Method | Endpoint | Mô tả | Quyền |
| :---: | :--- | :--- | :---: |
| `GET` | `/api/notifications/unread` | Lấy số lượng và danh sách thông báo chưa đọc | User |
| `POST` | `/api/notifications/{id}/read` | Đánh dấu thông báo đã đọc | User |

---

## 3. KÊNH ĐỒNG BỘ THỜI GIAN THỰC (WEBSOCKET STOMP)

- **WebSocket Endpoint:** `/ws-auction`
- **Giao thức:** STOMP over SockJS

| Kênh (Channel) | Chiều truyền | Mô tả dữ liệu đẩy |
| :--- | :---: | :--- |
| `/topic/auctions/{id}` | Server $\rightarrow$ Client (Broadcast) | Cập nhật giá mới (`current_price`), số lượt bid (`bid_count`), người dẫn đầu ẩn danh (`u***3`), thời gian còn lại. |
| `/topic/auctions/{id}/ended` | Server $\rightarrow$ Client (Broadcast) | Thông báo kết thúc phiên (`ENDED_SOLD` hoặc `ENDED_UNSOLD`). |
| `/user/queue/notifications` | Server $\rightarrow$ Client (Private) | Thông báo riêng cho từng user: Bị vượt giá (`OUTBID`), thắng phiên (`AUCTION_WON`), thanh toán thành công. |

