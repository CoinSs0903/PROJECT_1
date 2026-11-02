# 🔄 TÀI LIỆU SƠ ĐỒ TUẦN TỰ HỆ THỐNG (SEQUENCE DIAGRAMS)

> **Dự án:** Hệ Thống Đấu Giá Trực Tuyến (Cơ chế Đấu giá Tự động)  
> **Căn cứ:** `docs/SRS.md`, `docs/PLAN.md` (Ngày 3, 4), `docs/ERD.md`  
> **Quy tắc vàng:** Thể hiện rõ ranh giới giao dịch DB (`@Transactional`), điểm khóa dòng Concurrency (`SELECT ... FOR UPDATE`), sự kiện miền (Domain Events) và thông báo thời gian thực (WebSocket STOMP).

---

## 1. UC-01: ĐĂNG MÓN HÀNG VÀ THIẾT LẬP PHIÊN ĐẤU GIÁ (LISTING)

```mermaid
sequenceDiagram
    autonumber
    actor Seller as Người bán (Seller)
    participant Ctrl as AuctionController
    participant AucService as AuctionService
    participant CatRepo as CategoryRepository
    participant ItemRepo as ItemRepository
    participant AucRepo as AuctionRepository
    participant AuditService as AuditLogService

    Seller->>Ctrl: POST /auctions/create (item details, startingPrice, stepPrice, reservePrice, buyItNowPrice, durationDays)
    Ctrl->>AucService: createAuction(sellerId, request)
    
    rect rgb(240, 248, 255)
        note over AucService, AucRepo: GIAO DỊCH DATABASE (@Transactional) BẮT ĐẦU
        AucService->>CatRepo: findById(categoryId)
        CatRepo-->>AucService: Category
        
        %% Validate logic giá
        AucService->>AucService: validatePrices(startingPrice, reservePrice, buyItNowPrice)
        note over AucService: Kiểm tra: reservePrice >= startingPrice (nếu có)<br/>buyItNowPrice > reservePrice (nếu có)

        %% Lưu Item và ItemImages
        AucService->>ItemRepo: save(Item: sellerId, categoryId, title, desc, condition)
        ItemRepo-->>AucService: Item (persisted with id)

        %% Tạo Auction
        AucService->>AucService: calculateEndTime(startTime, durationDays)
        AucService->>AucRepo: save(Auction: itemId, currentPrice=startingPrice, status='ACTIVE' / 'SCHEDULED')
        AucRepo-->>AucService: Auction (persisted)

        AucService->>AuditService: logAction("CREATE_AUCTION", sellerId, auctionId)
        note over AucService, AucRepo: GIAO DỊCH DATABASE KẾT THÚC (COMMIT)
    end

    AucService-->>Ctrl: AuctionResponseDto
    Ctrl-->>Seller: 201 Created (Redirect đến trang chi tiết phiên)
```

---

## 2. UC-02: ĐẤU GIÁ TỰ ĐỘNG (PROXY BIDDING ENGINE)

```mermaid
sequenceDiagram
    autonumber
    actor Bidder as Người mua (Bidder B)
    actor HighBidder as Người đang dẫn đầu (Bidder A)
    participant Ctrl as BiddingController
    participant ProxyService as ProxyBiddingService
    participant AucRepo as AuctionRepository
    participant ProxyRepo as ProxyBidRepository
    participant BidRepo as BidRepository
    participant NotifFacade as NotificationFacade
    participant RealtimeService as WebSocketService

    Bidder->>Ctrl: POST /api/auctions/{id}/bids (maxAmount)
    Ctrl->>ProxyService: processBid(auctionId, bidderId, maxAmount)

    rect rgb(240, 248, 255)
        note over ProxyService, BidRepo: GIAO DỊCH DATABASE (@Transactional) BẮT ĐẦU
        
        %% Khóa bi quan dòng Auction để xử lý tuần tự tuyệt đối
        ProxyService->>AucRepo: findByIdForUpdate(auctionId) [PESSIMISTIC_WRITE LOCK]
        AucRepo-->>ProxyService: Auction (currentPrice, highestBidderId, status, stepPrice, reservePrice)
        
        ProxyService->>ProxyService: validateBid(auction, bidderId, maxAmount)
        note over ProxyService: Kiểm tra: status == ACTIVE<br/>bidderId != sellerId<br/>maxAmount >= currentPrice + stepPrice

        %% Lấy ProxyBid hiện tại của người dẫn đầu (Bidder A)
        ProxyService->>ProxyRepo: findTopByAuctionIdOrderByMaxAmountDesc(auctionId)
        ProxyRepo-->>ProxyService: ProxyBid A (bidderA, maxAmountA)

        alt Bidder B đặt giá trần cao hơn Bidder A (maxAmountB > maxAmountA)
            ProxyService->>ProxyService: calculateNewCurrentPrice(maxAmountA, stepPrice, reservePrice)
            note over ProxyService: newPrice = min(maxAmountB, maxAmountA + stepPrice)
            
            %% Cập nhật ProxyBid B
            ProxyService->>ProxyRepo: save(ProxyBid: bidderB, maxAmountB)
            
            %% Cập nhật Auction
            ProxyService->>AucService: updateAuction(currentPrice=newPrice, highestBidder=B, bidCount+1)
            
            %% Kiểm tra Reserve Price
            opt maxAmountB >= reservePrice && !auction.isReserveMet
                ProxyService->>AucRepo: update isReserveMet = TRUE
            end
            
            %% Ghi nhận bước giá công khai (Append-only)
            ProxyService->>BidRepo: save(Bid: bidderB, amount=newPrice, isAuto=true)
            
            %% Bắn thông báo Outbid cho người A
            ProxyService->>NotifFacade: send(bidderA, "OUTBID", "Bạn vừa bị vượt giá tại phiên #{id}")
            
        else Bidder B đặt giá trần thấp hơn hoặc bằng Bidder A (maxAmountB <= maxAmountA)
            %% Bidder A vẫn dẫn đầu nhưng giá bị đẩy lên
            ProxyService->>ProxyService: newPrice = min(maxAmountA, maxAmountB + stepPrice)
            ProxyService->>ProxyRepo: save(ProxyBid: bidderB, maxAmountB)
            ProxyService->>AucRepo: update currentPrice=newPrice, bidCount+1
            ProxyService->>BidRepo: save(Bid: bidderA, amount=newPrice, isAuto=true)
            
            %% Báo cho Bidder B biết ngay lập tức là đã bị outbid bởi giá trần đối thủ
            ProxyService->>NotifFacade: send(bidderB, "OUTBID_IMMEDIATELY", "Giá trần của bạn thấp hơn người dẫn đầu hiện tại")
        end

        note over ProxyService, BidRepo: GIAO DỊCH DATABASE KẾT THÚC (COMMIT)
    end

    %% Bắn cập nhật giá mới qua WebSocket cho tất cả người xem phòng đấu giá
    ProxyService->>RealtimeService: broadcastAuctionUpdate(auctionId, currentPrice, maskedBidder, bidCount)
    ProxyService-->>Ctrl: BidResultDto(success, currentPrice, isWinning)
    Ctrl-->>Bidder: 200 OK (Cập nhật giao diện đặt giá)
```

---

## 3. UC-03: MUA NGAY (BUY IT NOW)

```mermaid
sequenceDiagram
    autonumber
    actor Buyer as Người mua (Buyer)
    participant Ctrl as AuctionController
    participant BuyService as BuyItNowService
    participant AucRepo as AuctionRepository
    participant OrderService as OrderService
    participant RealtimeService as WebSocketService

    Buyer->>Ctrl: POST /api/auctions/{id}/buy-it-now
    Ctrl->>BuyService: executeBuyItNow(auctionId, buyerId)

    rect rgb(240, 248, 255)
        note over BuyService, AucRepo: GIAO DỊCH DATABASE (@Transactional) BẮT ĐẦU
        BuyService->>AucRepo: findByIdForUpdate(auctionId) [PESSIMISTIC_WRITE LOCK]
        AucRepo-->>BuyService: Auction
        
        BuyService->>BuyService: validateBuyItNowEligible(Auction)
        note over BuyService: Kiểm tra: status == ACTIVE<br/>buy_it_now_price != null<br/>chưa có lượt bid nào đạt reserve_price

        %% Đóng phiên lập tức
        BuyService->>AucRepo: update status='ENDED_SOLD', currentPrice=buyItNowPrice, highestBidderId=buyerId
        
        %% Khởi tạo Đơn hàng lập tức
        BuyService->>OrderService: createOrder(auctionId, buyerId, sellerId, finalPrice=buyItNowPrice, purchaseType='BUY_IT_NOW')
        OrderService-->>BuyService: Order (status=AWAITING_PAYMENT)
        
        note over BuyService, AucRepo: GIAO DỊCH DATABASE KẾT THÚC (COMMIT)
    end

    BuyService->>RealtimeService: broadcastAuctionEnded(auctionId, "SOLD_BUY_IT_NOW", buyerId)
    BuyService-->>Ctrl: OrderCreatedDto(orderId)
    Ctrl-->>Buyer: 200 OK (Chuyển hướng đến màn hình thanh toán đơn hàng)
```

---

## 4. UC-04: ĐÓNG PHIÊN TỰ ĐỘNG & CHỐT NGƯỜI THẮNG (AUCTION CLOSER)

```mermaid
sequenceDiagram
    autonumber
    participant Scheduler as Spring Scheduler (mỗi 5 giây)
    participant CloserService as AuctionCloserService
    participant AucRepo as AuctionRepository
    participant OrderService as OrderService
    participant NotifFacade as NotificationFacade
    participant RealtimeService as WebSocketService

    Scheduler->>CloserService: scanAndCloseExpiredAuctions()
    CloserService->>AucRepo: findActiveAuctionsPastEndTime(now)
    AucRepo-->>CloserService: List<Auction>

    loop Từng phiên hết hạn
        rect rgb(240, 248, 255)
            note over CloserService, AucRepo: GIAO DỊCH (@Transactional) TỪNG PHIÊN
            CloserService->>AucRepo: findByIdForUpdate(auctionId)
            
            alt Có người trả giá VÀ Đạt Reserve Price (hoặc không cài Reserve Price)
                CloserService->>AucRepo: update status='ENDED_SOLD'
                CloserService->>OrderService: createOrder(auctionId, highestBidderId, sellerId, currentPrice)
                
                %% Thông báo thắng cho Buyer & bán thành công cho Seller
                CloserService->>NotifFacade: send(highestBidderId, "AUCTION_WON", "Bạn đã trúng phiên #{id}")
                CloserService->>NotifFacade: send(sellerId, "ITEM_SOLD", "Sản phẩm #{id} đã bán thành công")
                CloserService->>RealtimeService: broadcastAuctionEnded(auctionId, "ENDED_SOLD")
            else Không ai bid HOẶC Chưa đạt Reserve Price
                CloserService->>AucRepo: update status='ENDED_UNSOLD'
                CloserService->>NotifFacade: send(sellerId, "AUCTION_UNSOLD", "Phiên kết thúc không thành công")
                CloserService->>RealtimeService: broadcastAuctionEnded(auctionId, "ENDED_UNSOLD")
            end
        end
    end
```

---

## 5. UC-05: THANH TOÁN ĐƠN HÀNG QUA VÍ (CHECKOUT & ESCROW PAYMENT)

```mermaid
sequenceDiagram
    autonumber
    actor Buyer as Người mua (Buyer)
    participant Ctrl as OrderController
    participant OrderService as OrderService
    participant WalletFacade as WalletFacade
    participant WallRepo as WalletRepository
    participant TxRepo as TransactionRepository
    participant NotifFacade as NotificationFacade

    Buyer->>Ctrl: POST /api/orders/{id}/pay (shippingAddress)
    Ctrl->>OrderService: processPayment(orderId, buyerId, shippingAddress)

    rect rgb(240, 248, 255)
        note over OrderService, TxRepo: GIAO DỊCH DATABASE (@Transactional) BẮT ĐẦU
        OrderService->>OrderService: validateOrder(AWAITING_PAYMENT, within 72h)
        
        %% Trừ tiền ví Buyer và giữ vào Escrow
        OrderService->>WalletFacade: payOrder(buyerId, orderId, totalAmount)
        WalletFacade->>WallRepo: findByUserIdForUpdate(buyerId) [LOCK]
        WallRepo-->>WalletFacade: Wallet (availableBalance)
        
        alt availableBalance >= totalAmount
            WalletFacade->>WallRepo: update availableBalance -= totalAmount
            WalletFacade->>TxRepo: save(Transaction: PAYMENT, refType='ORDER', refId=orderId)
            
            %% Cập nhật trạng thái đơn hàng
            OrderService->>OrderService: update status='PAID', shippingAddress
            OrderService->>NotifFacade: send(sellerId, "ORDER_PAID", "Người mua đã thanh toán, hãy chuẩn bị giao hàng")
        else Số dư không đủ
            WalletFacade-->>OrderService: throw InsufficientBalanceException
            OrderService-->>Ctrl: 400 Bad Request (Số dư không đủ, gợi ý nạp tiền)
        end
        note over OrderService, TxRepo: GIAO DỊCH DATABASE KẾT THÚC (COMMIT)
    end
```

---

## 6. UC-06: XỬ LÝ BÙNG HÀNG (UNPAID ITEM) & ĐĂNG LẠI PHIÊN (RELIST)

```mermaid
sequenceDiagram
    autonumber
    participant Scheduler as OrderTimeoutScheduler
    participant OrderService as OrderService
    participant UserRepo as UserRepository
    actor Seller as Người bán (Seller)
    participant AucCtrl as AuctionController
    participant RelistService as RelistService

    %% Giai đoạn 1: Quét quá hạn
    Scheduler->>OrderService: scanOverdueUnpaidOrders()
    rect rgb(240, 248, 255)
        OrderService->>OrderService: update status='CANCELLED_UNPAID'
        OrderService->>UserRepo: incrementUnpaidStrikes(buyerId)
        note over UserRepo: unpaid_strikes_count += 1<br/>Nếu >= 3 gậy: status='RESTRICTED_BIDDING'
    end

    %% Giai đoạn 2: Seller thực hiện Relist món hàng
    Seller->>AucCtrl: POST /auctions/relist/{itemId} (newPricing, duration)
    AucCtrl->>RelistService: relistItem(sellerId, itemId, request)
    rect rgb(240, 248, 255)
        RelistService->>RelistService: validateItemBelongsToSeller(itemId, sellerId)
        RelistService->>RelistService: createNewAuctionRecord(itemId, prices, duration)
    end
    RelistService-->>AucCtrl: NewAuctionId
    AucCtrl-->>Seller: 201 Created (Phiên đấu giá mới đã hoạt động)
```
