# ðŸ“˜ TÃ€I LIá»†U GIáº¢I THÃCH NGHIá»†P Vá»¤ ERD & SO SÃNH MÃ” HÃŒNH Sàn đấu giá trực tuyến

> **Dá»± Ã¡n:** Há»‡ Thá»‘ng Äáº¥u GiÃ¡ Trá»±c Tuyáº¿n (Online Auction System - `project_1`)  
> **MÃ´n há»c:** PhÃ¡t Triá»ƒn Pháº§n Má»m HÆ°á»›ng Äá»‘i TÆ°á»£ng (PTPMHÄT)  
> **TÃ i liá»‡u liÃªn quan:** [ERD.md](./ERD.md) | [STATE_MACHINES.md](./STATE_MACHINES.md) | [SRS.md](./SRS.md)

---

## Má»¤C Lá»¤C
1. [NguyÃªn táº¯c Thiáº¿t káº¿ Cá»‘t lÃµi (Database Conventions)](#1-nguyÃªn-táº¯c-thiáº¿t-káº¿-cá»‘t-lÃµi-database-conventions)
2. [Giáº£i thÃ­ch Chi tiáº¿t 19 Báº£ng theo 5 PhÃ¢n há»‡ Nghiá»‡p vá»¥](#2-giáº£i-thÃ­ch-chi-tiáº¿t-19-báº£ng-theo-5-phÃ¢n-há»‡-nghiá»‡p-vá»¥)
3. [Luá»“ng Nghiá»‡p vá»¥ & Biáº¿n Ä‘á»•i Dá»¯ liá»‡u 6 Giai Ä‘oáº¡n (`project_1`)](#3-luá»“ng-nghiá»‡p-vá»¥--biáº¿n-Ä‘á»•i-dá»¯-liá»‡u-6-giai-Ä‘oáº¡n-project_1)
4. [PhÃ¢n tÃ­ch Má»Ÿ rá»™ng: Chuyá»ƒn Ä‘á»•i ERD sang MÃ´ hÃ¬nh Äáº¥u giÃ¡ Sàn đấu giá trực tuyến](#4-phÃ¢n-tÃ­ch-má»Ÿ-rá»™ng-chuyá»ƒn-Ä‘á»•i-erd-sang-mÃ´-hÃ¬nh-Ä‘áº¥u-giÃ¡-Sàn đấu giá trực tuyến)
5. [Ghi chÃº Trá»ng tÃ¢m khi BÃ¡o cÃ¡o / Váº¥n Ä‘Ã¡p Äá»“ Ã¡n (Notes)](#5-ghi-chÃº-trá»ng-tÃ¢m-khi-bÃ¡o-cÃ¡o--váº¥n-Ä‘Ã¡p-Ä‘á»“-Ã¡n-notes)

---

## 1. NGUYÃŠN Táº®C THIáº¾T Káº¾ Cá»T LÃ•I (DATABASE CONVENTIONS)

TrÆ°á»›c khi Ä‘i vÃ o tá»«ng báº£ng, ERD cá»§a `project_1` tuÃ¢n thá»§ **5 nguyÃªn táº¯c ká»¹ thuáº­t báº¥t biáº¿n** nháº±m Ä‘áº£m báº£o tÃ­nh toÃ n váº¹n tÃ i chÃ­nh vÃ  phÃ¡p lÃ½:

| STT | NguyÃªn táº¯c | Thiáº¿t káº¿ trong ERD | LÃ½ do nghiá»‡p vá»¥ |
| :-: | :--- | :--- | :--- |
| **1** | **Tiá»n tá»‡ chÃ­nh xÃ¡c tuyá»‡t Ä‘á»‘i** | DÃ¹ng kiá»ƒu `BIGINT` (Ä‘Æ¡n vá»‹: VNÄ), cÃ³ rÃ ng buá»™c `CHECK (amount >= 0)`. | TrÃ¡nh hoÃ n toÃ n sai sá»‘ lÃ m trÃ²n dáº¥u pháº©y Ä‘á»™ng (`FLOAT`/`DOUBLE`) khi tÃ­nh tiá»n cá»c (5%â€“20%), phÃ­ dá»‹ch vá»¥ vÃ  hoÃ n tiá»n. |
| **2** | **Thá»i gian chuáº©n xÃ¡c Ä‘áº¿n mili-giÃ¢y** | DÃ¹ng `DATETIME(3)` theo mÃºi giá» chuáº©n `Asia/Ho_Chi_Minh`. | PhÃ¢n xá»­ cÃ´ng báº±ng thá»© tá»± Ä‘áº·t giÃ¡ (`server_time`) khi nhiá»u ngÆ°á»i cÃ¹ng tráº£ giÃ¡ á»Ÿ nhá»¯ng giÃ¢y cuá»‘i cÃ¹ng. |
| **3** | **Báº¥t biáº¿n lá»‹ch sá»­ (Append-only)** | CÃ¡c báº£ng `bids`, `transactions`, `audit_logs` chá»‰ cho phÃ©p `INSERT`, cáº¥m `UPDATE`/`DELETE`. | Äáº£m báº£o tÃ­nh minh báº¡ch, chá»‘ng sá»­a Ä‘á»•i lá»‹ch sá»­ tráº£ giÃ¡ hoáº·c lÃ m lá»‡ch sá»• cÃ¡i dÃ²ng tiá»n. |
| **4** | **Chá»‘ng láº·p giao dá»‹ch (Idempotency)** | Cá»™t `idempotency_key VARCHAR(100) UNIQUE` táº¡i báº£ng `transactions`. | NgÄƒn cháº·n lá»—i trá»« tiá»n hoáº·c hoÃ n tiá»n 2 láº§n khi máº¡ng cháº­p chá»n hoáº·c há»‡ thá»‘ng retry request. |
| **5** | **Báº£o máº­t danh tÃ­nh phÃ²ng Ä‘áº¥u giÃ¡** | Cá»™t `anonymous_code` (`UNIQUE(auction_id, anonymous_code)`) táº¡i báº£ng `registrations`. | Che giáº¥u danh tÃ­nh tháº­t cá»§a ngÆ°á»i mua trong suá»‘t thá»i gian diá»…n ra phiÃªn, chá»‘ng thÃ´ng Ä‘á»“ng dÃ¬m giÃ¡ hoáº·c Ä‘e dá»a. |

---

## 2. GIáº¢I THÃCH CHI TIáº¾T 19 Báº¢NG THEO 5 PHÃ‚N Há»† NGHIá»†P Vá»¤

Há»‡ thá»‘ng gá»“m **19 báº£ng** (14 báº£ng cá»‘t lÃµi tá»« SRS vÃ  5 báº£ng má»Ÿ rá»™ng há»— trá»£ nghiá»‡p vá»¥), Ä‘Æ°á»£c chia thÃ nh 5 phÃ¢n há»‡:

### 2.1. PhÃ¢n há»‡ NgÆ°á»i dÃ¹ng & Äá»‹nh danh (`users`, `seller_profiles`)
* **`users` (TÃ i khoáº£n ngÆ°á»i dÃ¹ng):**
  * LÆ°u thÃ´ng tin Ä‘á»‹nh danh cÃ¡ nhÃ¢n (`username`, `email`, `citizen_id` Ä‘á»u lÃ  `UNIQUE`) vÃ  tÃ i khoáº£n ngÃ¢n hÃ ng (`bank_name`, `bank_account_number`) Ä‘á»ƒ phá»¥c vá»¥ rÃºt tiá»n/nháº­n tiá»n.
  * Má»i ngÆ°á»i dÃ¹ng thÃ´ng thÆ°á»ng Ä‘á»u cÃ³ `role = 'USER'` (máº·c Ä‘á»‹nh cÃ³ quyá»n lÃ m NgÆ°á»i mua - Bidder). Quáº£n trá»‹ viÃªn cÃ³ `role = 'ADMIN'`.
* **`seller_profiles` (Há»“ sÆ¡ ngÆ°á»i bÃ¡n):**
  * Quan há»‡ **1 - 0..1** vá»›i `users` (`UNIQUE(user_id)`).
  * **Nghiá»‡p vá»¥:** Má»™t `USER` muá»‘n Ä‘Äƒng tÃ i sáº£n Ä‘áº¥u giÃ¡ pháº£i ná»™p há»“ sÆ¡ nÄƒng lá»±c/phÃ¡p lÃ½ vÃ o báº£ng nÃ y. Chá»‰ khi Admin xÃ©t duyá»‡t (`status = 'APPROVED'`), ngÆ°á»i dÃ¹ng Ä‘Ã³ má»›i Ä‘Æ°á»£c phÃ©p táº¡o tÃ i sáº£n á»Ÿ báº£ng `assets`.

### 2.2. PhÃ¢n há»‡ TÃ i sáº£n & PhiÃªn Ä‘áº¥u giÃ¡ (`assets`, `asset_images`, `auctions`, `listing_change_requests`, `auction_follows`, `auction_minutes`)
* **`assets` & `asset_images` (TÃ i sáº£n & ThÆ° viá»‡n áº£nh):**
  * LÆ°u thÃ´ng tin tÃ i sáº£n thuá»™c sá»Ÿ há»¯u cá»§a ngÆ°á»i bÃ¡n (`seller_id`).
  * PhÃ¢n loáº¡i `asset_type`: Äá»™ng sáº£n (`MOVABLE`) hoáº·c Báº¥t Ä‘á»™ng sáº£n (`IMMOVABLE`). Viá»‡c phÃ¢n loáº¡i nÃ y quyáº¿t Ä‘á»‹nh thá»i gian niÃªm yáº¿t tá»‘i thiá»ƒu theo luáº­t Ä‘á»‹nh.
* **`auctions` (PhiÃªn Ä‘áº¥u giÃ¡):**
  * Quan há»‡ **1 - 1** vá»›i `assets` (`UNIQUE(asset_id)`).
  * Há»— trá»£ 2 phÆ°Æ¡ng thá»©c Ä‘áº¥u giÃ¡ (`method`):
    1. **Tráº£ giÃ¡ lÃªn (`ASCENDING`):** Sá»­ dá»¥ng `starting_price` (giÃ¡ khá»Ÿi Ä‘iá»ƒm) vÃ  `step_price` (bÆ°á»›c giÃ¡ tá»‘i thiá»ƒu má»—i láº§n tráº£ thÃªm).
    2. **Äáº·t giÃ¡ xuá»‘ng (`DESCENDING`):** GiÃ¡ giáº£m tá»± Ä‘á»™ng tá»« `starting_price` theo tá»«ng má»©c `step_price` sau má»—i khoáº£ng thá»i gian `price_reduction_interval_seconds`, nhÆ°ng khÃ´ng tháº¥p hÆ¡n giÃ¡ sÃ n `floor_price`.
  * LÆ°u trá»¯ trá»±c tiáº¿p `current_price` vÃ  `highest_bidder_id` ngay trÃªn báº£ng Ä‘á»ƒ tá»‘i Æ°u tá»‘c Ä‘á»™ Ä‘á»c thá»i gian thá»±c trong phÃ²ng Ä‘áº¥u giÃ¡ mÃ  khÃ´ng cáº§n `MAX(amount)` trÃªn báº£ng `bids`.
* **CÃ¡c báº£ng má»Ÿ rá»™ng vÃ²ng Ä‘á»i phiÃªn:**
  * **`listing_change_requests`:** LÆ°u yÃªu cáº§u xin thay Ä‘á»•i thÃ´ng sá»‘ phiÃªn Ä‘áº¥u giÃ¡ khi Ä‘Ã£ niÃªm yáº¿t (kÃ¨m áº£nh chá»¥p JSON `old_values`, `new_values`) Ä‘á»ƒ Admin duyá»‡t.
  * **`auction_follows`:** LÆ°u danh sÃ¡ch phiÃªn Ä‘áº¥u giÃ¡ mÃ  ngÆ°á»i dÃ¹ng báº¥m theo dÃµi Ä‘á»ƒ nháº­n thÃ´ng bÃ¡o nháº¯c lá»‹ch.
  * **`auction_minutes`:** BiÃªn báº£n Ä‘áº¥u giÃ¡ Ä‘iá»‡n tá»­ (quan há»‡ 1-1 vá»›i `auctions`), tá»± Ä‘á»™ng chá»¥p toÃ n bá»™ káº¿t quáº£ phiÃªn dÆ°á»›i dáº¡ng JSON (`snapshot_data`) khi phiÃªn káº¿t thÃºc (`COMPLETED` hoáº·c `FAILED`).

### 2.3. PhÃ¢n há»‡ Tham gia, Äáº·t trÆ°á»›c, Tráº£ giÃ¡ & Quyáº¿t toÃ¡n (`registrations`, `deposits`, `bids`, `settlements`)
* **`registrations` (Há»“ sÆ¡ Ä‘Äƒng kÃ½ tham gia phiÃªn):**
  * RÃ ng buá»™c `UNIQUE(auction_id, bidder_id)`: Má»—i ngÆ°á»i chá»‰ Ä‘Æ°á»£c Ä‘Äƒng kÃ½ tá»‘i Ä‘a 1 láº§n cho 1 phiÃªn.
  * Cáº¥p mÃ£ áº©n danh 6 chá»¯ sá»‘ `anonymous_code` duy nháº¥t trong phiÃªn.
  * Cá»™t `decision_on_listing_change` (`CONTINUE` / `DECLINE`): Ghi nháº­n quyáº¿t Ä‘á»‹nh Ä‘i tiáº¿p hay rÃºt lui nháº­n láº¡i cá»c cá»§a ngÆ°á»i mua náº¿u phiÃªn Ä‘áº¥u giÃ¡ cÃ³ sá»± thay Ä‘á»•i thÃ´ng tin niÃªm yáº¿t.
* **`deposits` (Tiá»n Ä‘áº·t trÆ°á»›c):**
  * Quan há»‡ **1 - 1** vá»›i `registrations` (`UNIQUE(registration_id)`).
  * Quáº£n lÃ½ khoáº£n tiá»n Ä‘áº·t trÆ°á»›c (tá»« 5% Ä‘áº¿n 20% giÃ¡ khá»Ÿi Ä‘iá»ƒm): tá»« lÃºc Ä‘ang phong tá»a (`HELD`), hoÃ n tráº£ cho ngÆ°á»i thua (`REFUNDED`), chuyá»ƒn thÃ nh tiá»n cá»c cá»§a ngÆ°á»i tháº¯ng (`CONVERTED_TO_SELLER_DEPOSIT`), hoáº·c tá»‹ch thu náº¿u vi pháº¡m/bá» cá»c (`FORFEITED`) Ä‘á»ƒ chuyá»ƒn cho ngÆ°á»i bÃ¡n (`TRANSFERRED_TO_SELLER`).
* **`bids` (Lá»‹ch sá»­ tráº£ giÃ¡ báº¥t biáº¿n):**
  * LÆ°u **táº¥t cáº£** cÃ¡c lÆ°á»£t gá»­i giÃ¡ cá»§a ngÆ°á»i mua theo `server_time`, bao gá»“m cáº£ lÆ°á»£t há»£p lá»‡ (`is_valid = TRUE`) vÃ  khÃ´ng há»£p lá»‡ (`is_valid = FALSE` kÃ¨m lÃ½ do `invalid_reason` nhÆ° tháº¥p hÆ¡n bÆ°á»›c giÃ¡, gá»­i cháº­m hÆ¡n ngÆ°á»i khÃ¡c...).
* **`settlements` (Thanh toÃ¡n káº¿t quáº£ trÃºng Ä‘áº¥u giÃ¡):**
  * Sinh ra ngay khi xÃ¡c Ä‘á»‹nh Ä‘Æ°á»£c ngÆ°á»i tháº¯ng. TÃ­nh toÃ¡n nghÄ©a vá»¥ tÃ i chÃ­nh cÃ²n láº¡i:
    $$\text{remaining\_amount} = \text{winning\_price} - \text{deposit\_deducted}$$
  * Theo dÃµi háº¡n thanh toÃ¡n (`payment_deadline`) vÃ  tráº¡ng thÃ¡i hoÃ n táº¥t (`WAITING_FOR_PAYMENT` â†’ `PAID` / `CANCELED` / `OVERDUE`).

### 2.4. PhÃ¢n há»‡ TÃ i chÃ­nh & VÃ­ Ä‘iá»‡n tá»­ (`wallets`, `transactions`)
* **`wallets` (VÃ­ tiá»n):**
  * Gá»“m vÃ­ cÃ¡ nhÃ¢n cá»§a ngÆ°á»i dÃ¹ng (`wallet_type = 'USER'`, `UNIQUE(user_id)`) vÃ  2 vÃ­ há»‡ thá»‘ng (`user_id = NULL`): **VÃ­ kÃ½ quá»¹ (`SYSTEM_ESCROW`)** vÃ  **VÃ­ doanh thu phÃ­ sÃ n (`SYSTEM_REVENUE`)**.
  * TÃ¡ch biá»‡t rÃµ 2 ngÄƒn tiá»n:
    * `available_balance`: Sá»‘ dÆ° kháº£ dá»¥ng (cÃ³ thá»ƒ rÃºt hoáº·c dÃ¹ng Ä‘á»ƒ Ä‘Äƒng kÃ½ phiÃªn má»›i).
    * `frozen_balance`: Sá»‘ dÆ° Ä‘ang bá»‹ Ä‘Ã³ng bÄƒng (Ä‘ang lÃ m tiá»n Ä‘áº·t trÆ°á»›c cho cÃ¡c phiÃªn chÆ°a káº¿t thÃºc).
* **`transactions` (Sá»• cÃ¡i giao dá»‹ch tiá»n báº¥t biáº¿n):**
  * Ghi váº¿t má»i biáº¿n Ä‘á»™ng sá»‘ dÆ° (`balance_before`, `balance_after`) theo 8 loáº¡i giao dá»‹ch: `TOPUP` (náº¡p), `WITHDRAW` (rÃºt), `FREEZE` (Ä‘Ã³ng bÄƒng cá»c), `UNFREEZE` (giáº£i tá»a cá»c), `CONVERT_DEPOSIT` (kháº¥u trá»« cá»c), `PAYMENT` (thanh toÃ¡n tiá»n trÃºng), `SERVICE_FEE` (thu phÃ­ sÃ n), `PAYOUT_TO_SELLER` (giáº£i ngÃ¢n cho ngÆ°á»i bÃ¡n).
  * LiÃªn káº¿t Ä‘a hÃ¬nh (`reference_type` + `reference_id`) trá» vá» báº£n ghi `deposits` hoáº·c `settlements` tÆ°Æ¡ng á»©ng.

### 2.5. PhÃ¢n há»‡ Váº­n hÃ nh, Kiá»ƒm toÃ¡n & Cáº¥u hÃ¬nh (`notifications`, `audit_logs`, `complaints`, `system_configs`, `holidays`)
* **`notifications`:** LÆ°u thÃ´ng bÃ¡o gá»­i tá»›i ngÆ°á»i dÃ¹ng (khi Ä‘Æ°á»£c duyá»‡t há»“ sÆ¡, bá»‹ vÆ°á»£t giÃ¡, trÃºng Ä‘áº¥u giÃ¡, hoÃ n cá»c...).
* **`complaints`:** Quáº£n lÃ½ Ä‘Æ¡n khiáº¿u náº¡i cá»§a Bidder hoáº·c Seller Ä‘á»‘i vá»›i má»™t phiÃªn Ä‘áº¥u giÃ¡ kÃ¨m Ä‘Æ°á»ng dáº«n báº±ng chá»©ng (`evidence_url`) vÃ  káº¿t luáº­n xá»­ lÃ½ cá»§a Admin (`resolution_note`).
* **`audit_logs`:** Nháº­t kÃ½ kiá»ƒm toÃ¡n toÃ n há»‡ thá»‘ng, tá»± Ä‘á»™ng chá»¥p JSON `data_before` vÃ  `data_after` kÃ¨m Ä‘á»‹a chá»‰ IP má»—i khi cÃ³ thay Ä‘á»•i tráº¡ng thÃ¡i quan trá»ng.
* **`system_configs` & `holidays`:** LÆ°u tham sá»‘ váº­n hÃ nh Ä‘á»™ng (tá»· lá»‡ phÃ­ dá»‹ch vá»¥, thá»i gian chá» thanh toÃ¡n) vÃ  danh sÃ¡ch ngÃ y lá»… nhÃ  nÆ°á»›c Ä‘á»ƒ tÃ­nh chÃ­nh xÃ¡c **ngÃ y lÃ m viá»‡c** (Business Days).

---

## 3. LUá»’NG NGHIá»†P Vá»¤ & BIáº¾N Äá»”I Dá»® LIá»†U 6 GIAI ÄOáº N (`project_1`)

```mermaid
sequenceDiagram
    autonumber
    actor Seller as NgÆ°á»i bÃ¡n (Seller)
    actor Admin as Quáº£n trá»‹ viÃªn (Admin)
    actor Bidder as NgÆ°á»i mua (Bidder)
    participant DB as CÆ¡ sá»Ÿ dá»¯ liá»‡u (ERD)
    participant System as Há»‡ thá»‘ng (Scheduler)

    Seller->>DB: 1. Ná»™p há»“ sÆ¡ ngÆ°á»i bÃ¡n (seller_profiles: PENDING)
    Admin->>DB: 2. Duyá»‡t há»“ sÆ¡ (seller_profiles: APPROVED)
    Seller->>DB: 3. Táº¡o tÃ i sáº£n (assets) & phiÃªn Ä‘áº¥u giÃ¡ (auctions: PENDING_APPROVAL)
    Admin->>DB: 4. Duyá»‡t niÃªm yáº¿t cÃ´ng khai (auctions: LISTED)
    Bidder->>DB: 5. Náº¡p tiá»n (wallets) & ÄÄƒng kÃ½ (registrations + deposits: HELD, FREEZE tiá»n)
    System->>DB: 6. TrÆ°á»›c má»Ÿ phiÃªn 2 ngÃ y LV: ÄÃ³ng Ä‘Äƒng kÃ½ (auctions: WAITING_FOR_OPENING)
    System->>DB: 7. Äáº¿n start_time: Má»Ÿ phÃ²ng Ä‘áº¥u giÃ¡ (auctions: IN_PROGRESS)
    Bidder->>DB: 8. Gá»­i lá»‡nh tráº£ giÃ¡/cháº¥p nháº­n giÃ¡ (bids -> cáº­p nháº­t current_price)
    System->>DB: 9. Chá»‘t ngÆ°á»i tháº¯ng (auctions: WAITING_FOR_PAYMENT, táº¡o settlements)
    System->>DB: 10. HoÃ n tiá»n Ä‘áº·t trÆ°á»›c cho ngÆ°á»i thua (deposits: REFUNDED, UNFREEZE tiá»n)
    Bidder->>DB: 11. NgÆ°á»i tháº¯ng tráº£ pháº§n tiá»n cÃ²n láº¡i (settlements: PAID)
    System->>DB: 12. Giáº£i ngÃ¢n cho Seller, thu phÃ­ sÃ n & xuáº¥t biÃªn báº£n (auctions: COMPLETED, auction_minutes)
```

### Giai Ä‘oáº¡n 1: Chuáº©n bá»‹ & NiÃªm yáº¿t (`PENDING_APPROVAL` â†’ `LISTED`)
1. NgÆ°á»i dÃ¹ng Ä‘Æ°á»£c duyá»‡t `seller_profiles` (`APPROVED`) táº¡o báº£n ghi trong `assets` vÃ  `asset_images`.
2. Seller táº¡o phiÃªn trong `auctions` (`status = 'PENDING_APPROVAL'`), chá»n phÆ°Æ¡ng thá»©c `ASCENDING` hoáº·c `DESCENDING`, thiáº¿t láº­p giÃ¡ khá»Ÿi Ä‘iá»ƒm vÃ  tá»· lá»‡ tiá»n Ä‘áº·t trÆ°á»›c (5%â€“20%).
3. Admin kiá»ƒm tra tÃ­nh há»£p lá»‡ (Ä‘á»‘i chiáº¿u báº£ng `holidays` Ä‘á»ƒ Ä‘áº£m báº£o thá»i gian niÃªm yáº¿t Ä‘á»§ sá»‘ ngÃ y lÃ m viá»‡c theo quy Ä‘á»‹nh) vÃ  duyá»‡t phiÃªn sang `status = 'LISTED'`, ghi nháº­n `listing_at` vÃ  `registration_deadline` (trÆ°á»›c `start_time` 2 ngÃ y lÃ m viá»‡c).

### Giai Ä‘oáº¡n 2: ÄÄƒng kÃ½ & Phong tá»a Tiá»n Ä‘áº·t trÆ°á»›c (`LISTED` â†’ `WAITING_FOR_OPENING`)
1. Bidder náº¡p tiá»n vÃ o `wallets` (`transactions`: `TOPUP`).
2. TrÆ°á»›c má»‘c `registration_deadline`, Bidder gá»­i yÃªu cáº§u Ä‘Äƒng kÃ½. Há»‡ thá»‘ng thá»±c thi trong **1 Database Transaction**:
   * Táº¡o báº£n ghi `registrations` kÃ¨m mÃ£ áº©n danh `anonymous_code`.
   * Táº¡o báº£n ghi `deposits` (`status = 'HELD'`, `amount = deposit_amount`).
   * Táº¡i báº£ng `wallets` cá»§a Bidder:
     $$\text{available\_balance} = \text{available\_balance} - \text{deposit\_amount}$$
     $$\text{frozen\_balance} = \text{frozen\_balance} + \text{deposit\_amount}$$
   * Ghi báº£n ghi `transactions` (`transaction_type = 'FREEZE'`).
3. ÄÃºng má»‘c `registration_deadline`, Scheduler tá»± Ä‘á»™ng chuyá»ƒn `auctions.status` sang `WAITING_FOR_OPENING` (khÃ³a cá»•ng Ä‘Äƒng kÃ½ má»›i).

### Giai Ä‘oáº¡n 3: Má»Ÿ phiÃªn & Äáº¥u giÃ¡ thá»i gian thá»±c (`WAITING_FOR_OPENING` â†’ `IN_PROGRESS`)
1. ÄÃºng giá» `start_time`, náº¿u khÃ´ng cÃ³ ngÆ°á»i Ä‘Äƒng kÃ½ há»£p lá»‡, phiÃªn chuyá»ƒn tháº³ng sang `FAILED` vÃ  hoÃ n tiá»n cá»c. NgÆ°á»£c láº¡i, phiÃªn chuyá»ƒn sang `IN_PROGRESS`.
2. **Vá»›i Äáº¥u giÃ¡ TÄƒng dáº§n (`ASCENDING`):**
   * Khi Bidder Ä‘áº·t giÃ¡, há»‡ thá»‘ng khÃ³a dÃ²ng `auctions` hiá»‡n táº¡i, kiá»ƒm tra `amount >= current_price + step_price`.
   * Náº¿u há»£p lá»‡: LÆ°u `bids` (`is_valid = TRUE`), cáº­p nháº­t `auctions.current_price = amount` vÃ  `auctions.highest_bidder_id = bidder_id`.
   * Náº¿u khÃ´ng há»£p lá»‡: LÆ°u `bids` (`is_valid = FALSE`, kÃ¨m `invalid_reason`).
3. **Vá»›i Äáº¥u giÃ¡ Giáº£m dáº§n (`DESCENDING`):**
   * Äá»‹nh ká»³ má»—i `price_reduction_interval_seconds`, Scheduler giáº£m `current_price` má»™t má»©c `step_price` (tá»‘i thiá»ƒu báº±ng `floor_price`).
   * NgÆ°á»i Ä‘áº§u tiÃªn gá»­i lá»‡nh `bid_type = 'ACCEPT'` há»£p lá»‡ sáº½ trá»Ÿ thÃ nh `highest_bidder_id` vÃ  káº¿t thÃºc viá»‡c tráº£ giÃ¡ ngay láº­p tá»©c.

### Giai Ä‘oáº¡n 4: Káº¿t thÃºc tráº£ giÃ¡ & HoÃ n cá»c ngÆ°á»i thua (`IN_PROGRESS` â†’ `WAITING_FOR_PAYMENT`)
1. Khi xÃ¡c Ä‘á»‹nh Ä‘Æ°á»£c ngÆ°á»i tháº¯ng (`highest_bidder_id`):
   * `auctions.status` chuyá»ƒn sang `WAITING_FOR_PAYMENT`.
   * Khoáº£n Ä‘áº·t trÆ°á»›c cá»§a ngÆ°á»i tháº¯ng trong `deposits` chuyá»ƒn tá»« `HELD` sang `CONVERTED_TO_SELLER_DEPOSIT`.
   * Táº¡o báº£n ghi `settlements` vá»›i `remaining_amount = winning_price - deposit_deducted` vÃ  háº¡n chÃ³t `payment_deadline`.
2. Äá»‘i vá»›i táº¥t cáº£ ngÆ°á»i tham gia **khÃ´ng trÃºng** (vÃ  khÃ´ng vi pháº¡m quy cháº¿):
   * `deposits.status` chuyá»ƒn sang `REFUNDED`.
   * Trong `wallets`: giáº£m `frozen_balance`, cá»™ng tráº£ láº¡i vÃ o `available_balance` (`transactions`: `UNFREEZE`).

### Giai Ä‘oáº¡n 5: Thanh toÃ¡n, Giáº£i ngÃ¢n & Xá»­ lÃ½ ngoáº¡i lá»‡ (`WAITING_FOR_PAYMENT` â†’ `COMPLETED` / `FAILED`)
* **TrÆ°á»ng há»£p 1 â€“ NgÆ°á»i tháº¯ng thanh toÃ¡n Ä‘Ãºng háº¡n (`PAID`):**
  1. Trá»« `remaining_amount` tá»« `available_balance` cá»§a ngÆ°á»i tháº¯ng (`PAYMENT`) vÃ  táº¥t toÃ¡n khoáº£n `frozen_balance` tiá»n cá»c (`CONVERT_DEPOSIT`).
  2. Cá»™ng phÃ­ dá»‹ch vá»¥ vÃ o vÃ­ `SYSTEM_REVENUE` (`SERVICE_FEE`) vÃ  cá»™ng tiá»n bÃ¡n tÃ i sáº£n (sau khi trá»« phÃ­) vÃ o `available_balance` cá»§a vÃ­ Seller (`PAYOUT_TO_SELLER`).
  3. Cáº­p nháº­t `deposits.status = 'TRANSFERRED_TO_SELLER'`, `settlements.status = 'PAID'`, `auctions.status = 'COMPLETED'`.
  4. Tá»± Ä‘á»™ng sinh biÃªn báº£n Ä‘iá»‡n tá»­ vÃ o báº£ng `auction_minutes`.
* **TrÆ°á»ng há»£p 2 â€“ NgÆ°á»i tháº¯ng bá» cá»c hoáº·c quÃ¡ háº¡n (`CANCELED` / `OVERDUE`):**
  1. Pháº¡t máº¥t cá»c: `deposits.status` cá»§a ngÆ°á»i vi pháº¡m chuyá»ƒn sang `FORFEITED`, tiá»n cá»c Ä‘Æ°á»£c chuyá»ƒn cho Seller (`TRANSFERRED_TO_SELLER`).
  2. Náº¿u lÃ  Ä‘áº¥u giÃ¡ **TÄƒng dáº§n**: XÃ©t trao quyá»n mua cho ngÆ°á»i tráº£ giÃ¡ cao liá»n ká». Náº¿u khÃ´ng cÃ²n ai Ä‘á»“ng Ã½ â†’ `auctions.status = 'FAILED'`.
  3. Náº¿u lÃ  Ä‘áº¥u giÃ¡ **Giáº£m dáº§n**: ÄÆ°a phiÃªn quay láº¡i tráº¡ng thÃ¡i `IN_PROGRESS` Ä‘á»ƒ tiáº¿p tá»¥c giáº£m giÃ¡ tá»« má»©c giÃ¡ vá»«a bá»‹ há»§y.

---

## 4. PHÃ‚N TÃCH Má»ž Rá»˜NG: CHUYá»‚N Äá»”I ERD SANG MÃ” HÃŒNH Äáº¤U GIÃ Sàn đấu giá trực tuyến

> [!IMPORTANT]
> **Káº¿t luáº­n Ä‘Ã¡nh giÃ¡:** ERD gá»‘c cá»§a `project_1` chá»‰ phÃ¹ há»£p khoáº£ng **40% â€“ 50%** náº¿u Ã¡p dá»¥ng cho sÃ n thÆ°Æ¡ng máº¡i Ä‘iá»‡n tá»­ Ä‘áº¥u giÃ¡ kiá»ƒu **Sàn đấu giá trực tuyến**.
> * **`project_1`:** MÃ´ hÃ¬nh **Äáº¥u giÃ¡ tÃ i sáº£n phÃ¡p lÃ½/nhÃ  nÆ°á»›c** (tiá»n kiá»ƒm cháº·t cháº½, báº¯t buá»™c Ä‘Äƒng kÃ½ trÆ°á»›c 2 ngÃ y lÃ m viá»‡c, ná»™p tiá»n Ä‘áº·t trÆ°á»›c 5%â€“20%, láº­p biÃªn báº£n phÃ¡p lÃ½).
> * **Sàn đấu giá trực tuyến:** MÃ´ hÃ¬nh **ThÆ°Æ¡ng máº¡i Ä‘iá»‡n tá»­ káº¿t há»£p Äáº¥u giÃ¡ tá»± do (C2C/B2C Marketplace & Proxy Bidding)** (khÃ´ng Ä‘Äƒng kÃ½ trÆ°á»›c, khÃ´ng Ä‘áº·t cá»c, tráº£ giÃ¡ tá»± Ä‘á»™ng, mua Ä‘á»©t ngay, giao nháº­n váº­n chuyá»ƒn vÃ  Ä‘Ã¡nh giÃ¡ uy tÃ­n 2 chiá»u).

### 4.1. 6 Äiá»ƒm khÃ´ng phÃ¹ há»£p cá»§a ERD gá»‘c khi Ã¡p vÃ o Sàn đấu giá trực tuyến
1. **Báº¯t buá»™c Ä‘Äƒng kÃ½ trÆ°á»›c & Äáº·t cá»c (`registrations` & `deposits`):** TrÃªn Sàn đấu giá trực tuyến, ngÆ°á»i mua tháº¥y mÃ³n hÃ ng lÃ  báº¥m tráº£ giÃ¡ ngay láº­p tá»©c (ká»ƒ cáº£ á»Ÿ giÃ¢y cuá»‘i cÃ¹ng), khÃ´ng pháº£i ná»™p há»“ sÆ¡ trÆ°á»›c 2 ngÃ y hay Ä‘Ã³ng bÄƒng 5%â€“20% tiá»n cá»c.
2. **Thiáº¿u cÆ¡ cháº¿ Äáº¥u giÃ¡ tá»± Ä‘á»™ng (`Proxy Bidding` / `Max Bid`):** NgÆ°á»i mua trÃªn Sàn đấu giá trực tuyến nháº­p má»©c giÃ¡ tá»‘i Ä‘a sáºµn sÃ ng tráº£ (giá»¯ bÃ­ máº­t), há»‡ thá»‘ng tá»± Ä‘á»™ng tráº£ giÃ¡ há»™ tá»«ng bÆ°á»›c nhá» nháº¥t Ä‘á»ƒ tháº¯ng Ä‘á»‘i thá»§. Báº£ng `bids` hiá»‡n táº¡i chá»‰ lÆ°u giÃ¡ trá»±c tiáº¿p.
3. **Thiáº¿u giÃ¡ Mua ngay (`buy_it_now_price`) vÃ  GiÃ¡ sÃ n áº©n (`reserve_price`):** TrÃªn Sàn đấu giá trực tuyến, ngÆ°á»i mua cÃ³ thá»ƒ mua Ä‘á»©t mÃ³n hÃ ng ngay láº­p tá»©c vá»›i giÃ¡ `Buy It Now`, vÃ  ngÆ°á»i bÃ¡n cÃ³ thá»ƒ Ä‘áº·t má»©c giÃ¡ ká»³ vá»ng tá»‘i thiá»ƒu áº©n (`Reserve Price`) mÃ  náº¿u chÆ°a cháº¡m tá»›i thÃ¬ khÃ´ng báº¯t buá»™c pháº£i bÃ¡n.
4. **RÃ ng buá»™c cá»©ng 1 TÃ i sáº£n â€“ 1 PhiÃªn (`UNIQUE(asset_id)` trÃªn báº£ng `auctions`):** TrÃªn Sàn đấu giá trực tuyến, má»™t mÃ³n hÃ ng Ä‘áº¥u giÃ¡ tháº¥t báº¡i cÃ³ thá»ƒ Ä‘Æ°á»£c Ä‘Äƒng Ä‘áº¥u giÃ¡ láº¡i (**Relist**) nhiá»u láº§n (quan há»‡ `1 - N`).
5. **Quy trÃ¬nh duyá»‡t tay & Lá»‹ch nghá»‰ lá»… (`holidays`, `auction_minutes`, `listing_change_requests`):** Sàn đấu giá trực tuyến hoáº¡t Ä‘á»™ng tá»± Ä‘á»™ng 24/7/365 toÃ n cáº§u theo sá»‘ ngÃ y thá»±c (3, 5, 7, 10 ngÃ y), khÃ´ng trá»« ngÃ y lá»… nhÃ  nÆ°á»›c vÃ  khÃ´ng cáº§n Admin duyá»‡t tá»«ng bÃ i Ä‘Äƒng.
6. **Thiáº¿u phÃ¢n há»‡ Giao váº­n, ÄÆ¡n hÃ ng & ÄÃ¡nh giÃ¡ uy tÃ­n (`orders`, `reviews`):** Sau khi tháº¯ng trÃªn Sàn đấu giá trực tuyến, tiá»n pháº£i Ä‘Æ°á»£c giá»¯ trong vÃ­ kÃ½ quá»¹ (`SYSTEM_ESCROW`) chá» Ä‘Ã³ng gÃ³i, váº­n chuyá»ƒn (`tracking_number`), giao hÃ ng thÃ nh cÃ´ng (`DELIVERED`) rá»“i má»›i giáº£i ngÃ¢n cho Seller, kÃ¨m há»‡ thá»‘ng cháº¥m Ä‘iá»ƒm uy tÃ­n 2 chiá»u (Feedback Score) Ä‘á»ƒ chá»‘ng bÃ¹ng hÃ ng.

### 4.2. CÃ¡ch thá»©c chá»‰nh sá»­a ERD sang mÃ´ hÃ¬nh Sàn đấu giá trực tuyến

| Thao tÃ¡c | Báº£ng tÃ¡c Ä‘á»™ng | Chi tiáº¿t chá»‰nh sá»­a |
| :--- | :--- | :--- |
| **1. Loáº¡i bá»** | `holidays`, `auction_minutes`, `listing_change_requests`, `registrations`, `deposits` | Loáº¡i bá» quy trÃ¬nh Ä‘Äƒng kÃ½ trÆ°á»›c, Ä‘áº·t cá»c báº¯t buá»™c vÃ  lá»‹ch nghá»‰ lá»… Ä‘á»ƒ cho phÃ©p ngÆ°á»i dÃ¹ng vÃ o tráº£ giÃ¡ tá»©c thÃ¬ (Instant Bidding). |
| **2. Chá»‰nh sá»­a** | `assets` $\rightarrow$ `items` | Bá» `asset_type`; thÃªm `category_id` (danh má»¥c), `item_condition` (`NEW`, `USED`, `REFURBISHED`), `weight_grams` (tÃ­nh phÃ­ ship). |
| **3. Chá»‰nh sá»­a** | `auctions` | Bá» `UNIQUE(asset_id)` (há»— trá»£ Relist), bá» `deposit_*`, `registration_deadline`. ThÃªm `buy_it_now_price`, `reserve_price`, `is_reserve_met`, `bid_count`. |
| **4. ThÃªm má»›i & NÃ¢ng cáº¥p** | `proxy_bids` & `bids` | ThÃªm báº£ng `proxy_bids` lÆ°u má»©c tráº§n bÃ­ máº­t (`max_amount`) cá»§a tá»«ng Bidder; thÃªm cá»™t `is_auto_bid` vÃ o báº£ng `bids` Ä‘á»ƒ ghi nháº­n cÃ¡c bÆ°á»›c nháº£y giÃ¡ tá»± Ä‘á»™ng. |
| **5. NÃ¢ng cáº¥p** | `settlements` $\rightarrow$ `orders` | Má»Ÿ rá»™ng thÃ nh báº£ng ÄÆ¡n hÃ ng TMÄT: thÃªm `purchase_type` (`AUCTION_WIN`, `BUY_IT_NOW`), `shipping_fee`, `shipping_address`, `tracking_number`, vÃ  tráº¡ng thÃ¡i giao váº­n (`AWAITING_PAYMENT` $\rightarrow$ `PAID_IN_ESCROW` $\rightarrow$ `SHIPPED` $\rightarrow$ `DELIVERED` $\rightarrow$ `COMPLETED`). |
| **6. ThÃªm má»›i** | `categories`, `reviews` | ThÃªm `categories` (danh má»¥c Ä‘a cáº¥p) vÃ  `reviews` (Ä‘Ã¡nh giÃ¡ uy tÃ­n 2 chiá»u Buyer $\leftrightarrow$ Seller sau khi hoÃ n táº¥t Ä‘Æ¡n hÃ ng). |

### 4.3. SÆ¡ Ä‘á»“ ERD sau khi chuyá»ƒn sang mÃ´ hÃ¬nh Sàn đấu giá trực tuyến

```mermaid
erDiagram
    users ||--o{ items : "Ä‘Äƒng bÃ¡n"
    users ||--o{ proxy_bids : "cÃ i giÃ¡ tráº§n tá»± Ä‘á»™ng"
    users ||--o{ bids : "lá»‹ch sá»­ bÆ°á»›c giÃ¡"
    users ||--o{ orders : "mua / thanh toÃ¡n"
    users ||--o{ reviews : "Ä‘Ã¡nh giÃ¡ uy tÃ­n"
    users ||--o| wallets : "sá»Ÿ há»¯u vÃ­"

    categories ||--o{ items : "phÃ¢n loáº¡i"
    items ||--o{ item_images : "cÃ³ nhiá»u áº£nh"
    items ||--o{ auctions : "Ä‘Æ°á»£c Ä‘áº¥u giÃ¡ (1-N, há»— trá»£ Relist)"

    auctions ||--o{ proxy_bids : "nháº­n má»©c tráº§n"
    auctions ||--o{ bids : "sinh bÆ°á»›c giÃ¡"
    auctions ||--o| orders : "táº¡o Ä‘Æ¡n hÃ ng khi tháº¯ng"

    orders ||--o{ reviews : "Ä‘Ã¡nh giÃ¡ 2 chiá»u"
    wallets ||--o{ transactions : "biáº¿n Ä‘á»™ng sá»‘ dÆ°"

    items {
        bigint id PK
        bigint seller_id FK
        bigint category_id FK
        varchar name
        varchar item_condition "NEW, USED, REFURBISHED"
        int weight_grams
    }

    auctions {
        bigint id PK
        bigint item_id FK "Bá» UNIQUE Ä‘á»ƒ Relist"
        bigint starting_price
        bigint step_price
        bigint reserve_price "GiÃ¡ sÃ n áº©n"
        bigint buy_it_now_price "GiÃ¡ mua Ä‘á»©t"
        boolean is_reserve_met
        bigint current_price
        bigint highest_bidder_id FK
        int bid_count
        datetime start_time
        datetime end_time
        varchar status
    }

    proxy_bids {
        bigint id PK
        bigint auction_id FK
        bigint bidder_id FK
        bigint max_amount "GiÃ¡ tá»‘i Ä‘a bÃ­ máº­t"
        datetime updated_at
    }

    orders {
        bigint id PK
        bigint auction_id FK "UK"
        bigint buyer_id FK
        bigint seller_id FK
        varchar purchase_type "AUCTION_WIN, BUY_IT_NOW"
        bigint item_price
        bigint shipping_fee
        bigint total_amount
        text shipping_address
        varchar tracking_number
        varchar status "AWAITING_PAYMENT, PAID_IN_ESCROW, SHIPPED, DELIVERED, COMPLETED"
    }

    reviews {
        bigint id PK
        bigint order_id FK
        bigint reviewer_id FK
        bigint reviewee_id FK
        int rating
        text comment
    }
```

### 4.4. So sÃ¡nh Äiá»ƒm máº¡nh & Äiá»ƒm yáº¿u giá»¯a 2 MÃ´ hÃ¬nh (Trade-offs)

| TiÃªu chÃ­ | MÃ´ hÃ¬nh ERD gá»‘c (`project_1` - Äáº¥u giÃ¡ PhÃ¡p lÃ½) | MÃ´ hÃ¬nh ERD cáº£i tiáº¿n (Kiá»ƒu Sàn đấu giá trực tuyến - Äáº¥u giÃ¡ TMÄT) |
| :--- | :--- | :--- |
| **RÃ o cáº£n tham gia** | **Cao:** Pháº£i Ä‘Äƒng kÃ½ trÆ°á»›c 2 ngÃ y lÃ m viá»‡c vÃ  ná»™p tiá»n Ä‘áº·t trÆ°á»›c 5%â€“20%. | **Ráº¥t tháº¥p (Äiá»ƒm máº¡nh lá»›n nháº¥t):** Tháº¥y hÃ ng lÃ  báº¥m tráº£ giÃ¡ ngay trong 1 giÃ¢y, tá»· lá»‡ chuyá»ƒn Ä‘á»•i (Conversion Rate) cá»±c cao. |
| **Rá»§i ro bÃ¹ng hÃ ng (Non-paying Bidder)** | **Ráº¥t tháº¥p (Äiá»ƒm máº¡nh lá»›n nháº¥t):** NgÆ°á»i tháº¯ng bá» cuá»™c sáº½ bá»‹ tá»‹ch thu ngay khoáº£n tiá»n Ä‘áº·t trÆ°á»›c (`FORFEITED`) Ä‘á»n bÃ¹ cho Seller. | **Cao (Äiá»ƒm yáº¿u lá»›n nháº¥t):** KhÃ´ng giá»¯ tiá»n cá»c trÆ°á»›c nÃªn ngÆ°á»i tháº¯ng dá»… bá» cháº¡y. Pháº£i kháº¯c phá»¥c báº±ng khÃ³a tÃ i khoáº£n (`unpaid_strikes`), giá»›i háº¡n háº¡n má»©c bid hoáº·c yÃªu cáº§u liÃªn káº¿t tháº» trÆ°á»›c. |
| **Tiá»‡n Ã­ch cho ngÆ°á»i mua & ngÆ°á»i bÃ¡n** | Äáº¥u giÃ¡ thá»§ cÃ´ng trá»±c tiáº¿p trong khung giá» cá»‘ Ä‘á»‹nh. | **Ráº¥t linh hoáº¡t:** CÃ³ `Proxy Bidding` tá»± Ä‘á»™ng tráº£ giÃ¡ há»™, `Buy It Now` mua Ä‘á»©t ngay, `Reserve Price` báº£o vá»‡ ngÆ°á»i bÃ¡n khá»i bÃ¡n lá»—. |
| **Kháº£ nÄƒng má»Ÿ rá»™ng (Scalability)** | Giá»›i háº¡n bá»Ÿi khÃ¢u Admin duyá»‡t tá»«ng há»“ sÆ¡ Ä‘Äƒng kÃ½ vÃ  duyá»‡t tá»«ng phiÃªn Ä‘áº¥u giÃ¡. | **Tá»± Ä‘á»™ng hÃ³a 100%:** Má»Ÿ rá»™ng lÃªn hÃ ng triá»‡u phiÃªn Ä‘áº¥u giÃ¡ Ä‘á»“ng thá»i mÃ  khÃ´ng cáº§n Admin duyá»‡t tay. |
| **Táº£i xá»­ lÃ½ Ä‘á»“ng thá»i (Concurrency)** | á»”n Ä‘á»‹nh do sá»‘ lÆ°á»£ng Bidder trong 1 phÃ²ng Ä‘Ã£ Ä‘Æ°á»£c chá»‘t cá»‘ Ä‘á»‹nh trÆ°á»›c 2 ngÃ y lÃ m viá»‡c. | **Ráº¥t kháº¯c nghiá»‡t á»Ÿ giÃ¢y cuá»‘i (Auction Sniping):** HÃ ng trÄƒm ngÆ°á»i cÃ³ thá»ƒ nháº£y vÃ o Ä‘áº·t lá»‡nh á»Ÿ 5 giÃ¢y cuá»‘i cÃ¹ng káº¿t há»£p tÃ­nh toÃ¡n `proxy_bids`, Ä‘Ã²i há»i khÃ³a bi quan (`FOR UPDATE`) hoáº·c hÃ ng Ä‘á»£i Redis. |
| **TÃ­nh tuÃ¢n thá»§ phÃ¡p luáº­t VN** | **Äáº¡t chuáº©n:** PhÃ¹ há»£p Ä‘áº¥u giÃ¡ tÃ i sáº£n cÃ´ng, Ä‘áº¥t Ä‘ai, biá»ƒn sá»‘ xe, tÃ i sáº£n thi hÃ nh Ã¡n theo Luáº­t Äáº¥u giÃ¡ tÃ i sáº£n. | **KhÃ´ng Ã¡p dá»¥ng Ä‘Æ°á»£c cho tÃ i sáº£n cÃ´ng phÃ¡p lÃ½:** Chá»‰ phÃ¹ há»£p lÃ m sÃ n thÆ°Æ¡ng máº¡i Ä‘iá»‡n tá»­ dÃ¢n sá»± (C2C/B2C). |

---

## 5. GHI CHÃš TRá»ŒNG TÃ‚M KHI BÃO CÃO / Váº¤N ÄÃP Äá»’ ÃN (NOTES)

> [!TIP]
> DÆ°á»›i Ä‘Ã¢y lÃ  5 cÃ¢u há»i pháº£n biá»‡n thÆ°á»ng gáº·p nháº¥t khi báº£o vá»‡ ERD cá»§a `project_1` vÃ  gá»£i Ã½ tráº£ lá»i ngáº¯n gá»n:

1. **CÃ¢u há»i 1: Táº¡i sao trong báº£ng `auctions` Ä‘Ã£ cÃ³ quan há»‡ vá»›i báº£ng `bids`, mÃ  láº¡i lÆ°u thÃªm `current_price` vÃ  `highest_bidder_id` gÃ¢y dÆ° thá»«a dá»¯ liá»‡u (Denormalization)?**
   * **Tráº£ lá»i:** ÄÃ¢y lÃ  ká»¹ thuáº­t **Denormalization cÃ³ chá»§ Ä‘Ã­ch Ä‘á»ƒ tá»‘i Æ°u hiá»‡u nÄƒng Ä‘á»c thá»i gian thá»±c**. Trong phÃ²ng Ä‘áº¥u giÃ¡ cÃ³ hÃ ng trÄƒm client truy váº¥n liÃªn tá»¥c má»—i giÃ¢y, viá»‡c Ä‘á»c trá»±c tiáº¿p `current_price` vÃ  `highest_bidder_id` trÃªn 1 dÃ²ng cá»§a báº£ng `auctions` (O(1)) nhanh hÆ¡n ráº¥t nhiá»u so vá»›i viá»‡c pháº£i cháº¡y truy váº¥n `SELECT MAX(amount) FROM bids WHERE auction_id = ? AND is_valid = TRUE` trÃªn báº£ng lá»‹ch sá»­ lá»›n. Äá»“ng thá»i, khÃ³a dÃ²ng (`SELECT ... FOR UPDATE`) trÃªn `auctions` cÅ©ng giÃºp kiá»ƒm soÃ¡t Concurrency táº¡i má»™t Ä‘iá»ƒm duy nháº¥t.

2. **CÃ¢u há»i 2: Táº¡i sao nhá»¯ng lÆ°á»£t tráº£ giÃ¡ bá»‹ lá»—i/khÃ´ng há»£p lá»‡ váº«n lÆ°u vÃ o báº£ng `bids` (`is_valid = FALSE`) mÃ  khÃ´ng cháº·n vÃ  bá» Ä‘i á»Ÿ táº§ng Service?**
   * **Tráº£ lá»i:** Trong nghiá»‡p vá»¥ Ä‘áº¥u giÃ¡ trá»±c tuyáº¿n, ngÆ°á»i mua ráº¥t hay khiáº¿u náº¡i: *"TÃ´i Ä‘Ã£ báº¥m tráº£ giÃ¡ cao hÆ¡n trÆ°á»›c khi háº¿t giá», táº¡i sao khÃ´ng Ä‘Æ°á»£c ghi nháº­n?"*. Viá»‡c lÆ°u láº¡i cáº£ cÃ¡c lÆ°á»£t tráº£ giÃ¡ bá»‹ tá»« chá»‘i kÃ¨m `server_time` (Ä‘á»™ chÃ­nh xÃ¡c mili-giÃ¢y) vÃ  `invalid_reason` vÃ o báº£ng báº¥t biáº¿n `bids` giÃºp há»‡ thá»‘ng cÃ³ **báº±ng chá»©ng phÃ¡p lÃ½ khÃ´ng thá»ƒ chá»‘i cÃ£i** khi giáº£i quyáº¿t khiáº¿u náº¡i (`complaints`).

3. **CÃ¢u há»i 3: LÃ m sao Ä‘áº£m báº£o tiá»n trong vÃ­ (`wallets`) khÃ´ng bao giá» bá»‹ Ã¢m hoáº·c bá»‹ trá»« 2 láº§n khi ngÆ°á»i dÃ¹ng báº¥m Ä‘Ãºp chuá»™t (Double-click)?**
   * **Tráº£ lá»i:** Há»‡ thá»‘ng káº¿t há»£p 3 lá»›p báº£o vá»‡:
     1. á»ž táº§ng Database: RÃ ng buá»™c `CHECK (available_balance >= 0)` vÃ  `CHECK (frozen_balance >= 0)`.
     2. á»ž táº§ng Giao dá»‹ch (`transactions`): Cá»™t `idempotency_key` cÃ³ rÃ ng buá»™c `UNIQUE` cháº·n má»i request trÃ¹ng láº·p.
     3. á»ž táº§ng Thá»±c thi Backend: Sá»­ dá»¥ng Transaction cÃ´ láº­p kÃ¨m khÃ³a dÃ²ng vÃ­ khi chuyá»ƒn tiá»n giá»¯a `available_balance` vÃ  `frozen_balance`.

4. **CÃ¢u há»i 4: Táº¡i sao khÃ´ng gá»™p báº£ng `registrations` vÃ  `deposits` lÃ m má»™t vÃ¬ chÃºng cÃ³ quan há»‡ 1-1?**
   * **Tráº£ lá»i:** TÃ¡ch riÃªng Ä‘á»ƒ Ä‘áº£m báº£o **NguyÃªn lÃ½ ÄÆ¡n trÃ¡ch nhiá»‡m (Single Responsibility Principle - SRP)** vÃ  tÃ¡ch biá»‡t 2 vÃ²ng Ä‘á»i tráº¡ng thÃ¡i Ä‘á»™c láº­p ([STATE_MACHINES.md](./STATE_MACHINES.md)):
     * `registrations` quáº£n lÃ½ **tÆ° cÃ¡ch phÃ¡p lÃ½ vÃ  Ä‘iá»ƒm danh phÃ²ng Ä‘áº¥u giÃ¡** (`PENDING_REVIEW` â†’ `VALID` â†’ `ATTENDED` / `DISQUALIFIED`).
     * `deposits` quáº£n lÃ½ **nghÄ©a vá»¥ tÃ i chÃ­nh vÃ  dÃ²ng tiá»n kÃ½ quá»¹** (`SUBMITTED`/`HELD` â†’ `REFUNDED` / `CONVERTED_TO_DEPOSIT` / `FORFEITED` â†’ `TRANSFERRED_TO_SELLER`).

5. **CÃ¢u há»i 5: Táº¡i sao báº£ng `transactions` dÃ¹ng `reference_type` vÃ  `reference_id` thay vÃ¬ táº¡o khÃ³a ngoáº¡i cá»©ng (`FK`) tá»›i `deposits` vÃ  `settlements`?**
   * **Tráº£ lá»i:** VÃ¬ má»™t giao dá»‹ch vÃ­ cÃ³ thá»ƒ phÃ¡t sinh tá»« nhiá»u nguá»“n nghiá»‡p vá»¥ khÃ¡c nhau: náº¡p/rÃºt tiá»n trá»±c tiáº¿p (`TOPUP`/`WITHDRAW`), phong tá»a tiá»n Ä‘áº·t trÆ°á»›c (`DEPOSIT`), hoáº·c thanh toÃ¡n káº¿t quáº£ trÃºng Ä‘áº¥u giÃ¡ (`SETTLEMENT`). Thiáº¿t káº¿ **Polymorphic Association (`reference_type` + `reference_id`)** giÃºp báº£ng sá»• cÃ¡i `transactions` gá»n gÃ ng, khÃ´ng bá»‹ chá»©a nhiá»u cá»™t khÃ³a ngoáº¡i `NULL` vÃ  dá»… dÃ ng má»Ÿ rá»™ng thÃªm cÃ¡c loáº¡i giao dá»‹ch má»›i trong tÆ°Æ¡ng lai.

