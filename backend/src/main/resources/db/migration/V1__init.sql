-- =============================================================================
-- V1__init.sql - Khởi tạo cấu trúc Cơ sở dữ liệu Hệ thống Đấu giá Trực tuyến
-- Khớp 100% với tài liệu docs/ERD.md (Nhiệm vụ Ngày 1)
-- =============================================================================

-- 1. Bảng người dùng (users)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    shipping_address TEXT,
    bank_name VARCHAR(100),
    bank_account_number VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    unpaid_strikes_count INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Bảng danh mục sản phẩm (categories)
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    display_order INT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Bảng món hàng đấu giá (items)
CREATE TABLE items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    item_condition VARCHAR(30) NOT NULL DEFAULT 'USED',
    main_image_url VARCHAR(500),
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_items_seller FOREIGN KEY (seller_id) REFERENCES users(id),
    CONSTRAINT fk_items_category FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Bảng album ảnh món hàng (item_images)
CREATE TABLE item_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_item_images_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Bảng phiên đấu giá (auctions)
-- Lưu ý: Không đặt UNIQUE(item_id) để hỗ trợ tính năng Relist nhiều lần cho cùng 1 item
CREATE TABLE auctions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    starting_price BIGINT NOT NULL,
    step_price BIGINT NOT NULL,
    reserve_price BIGINT NULL,
    buy_it_now_price BIGINT NULL,
    is_reserve_met BOOLEAN NOT NULL DEFAULT FALSE,
    current_price BIGINT NOT NULL,
    highest_bidder_id BIGINT NULL,
    bid_count INT NOT NULL DEFAULT 0,
    start_time DATETIME(3) NOT NULL,
    end_time DATETIME(3) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    rejection_reason TEXT,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_auctions_item FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_auctions_highest_bidder FOREIGN KEY (highest_bidder_id) REFERENCES users(id),
    CONSTRAINT chk_auctions_starting_price CHECK (starting_price >= 0),
    CONSTRAINT chk_auctions_step_price CHECK (step_price > 0),
    CONSTRAINT chk_auctions_current_price CHECK (current_price >= starting_price)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Bảng mức giá trần tối đa bí mật (proxy_bids)
CREATE TABLE proxy_bids (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    bidder_id BIGINT NOT NULL,
    max_amount BIGINT NOT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_proxy_bids_auction FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE,
    CONSTRAINT fk_proxy_bids_bidder FOREIGN KEY (bidder_id) REFERENCES users(id),
    CONSTRAINT uq_proxy_bids_auction_bidder UNIQUE (auction_id, bidder_id),
    CONSTRAINT chk_proxy_bids_max_amount CHECK (max_amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Bảng lịch sử bước giá công khai (bids)
CREATE TABLE bids (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    bidder_id BIGINT NOT NULL,
    bid_amount BIGINT NOT NULL,
    is_auto_bid BOOLEAN NOT NULL DEFAULT FALSE,
    bid_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_bids_auction FOREIGN KEY (auction_id) REFERENCES auctions(id),
    CONSTRAINT fk_bids_bidder FOREIGN KEY (bidder_id) REFERENCES users(id),
    CONSTRAINT chk_bids_bid_amount CHECK (bid_amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Bảng đơn hàng sau thắng hoặc mua ngay (orders)
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL UNIQUE,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    purchase_type VARCHAR(30) NOT NULL DEFAULT 'AUCTION_WIN',
    final_price BIGINT NOT NULL,
    shipping_fee BIGINT NOT NULL DEFAULT 0,
    total_amount BIGINT NOT NULL,
    shipping_address TEXT,
    tracking_number VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'AWAITING_PAYMENT',
    payment_deadline DATETIME(3) NOT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_orders_auction FOREIGN KEY (auction_id) REFERENCES auctions(id),
    CONSTRAINT fk_orders_buyer FOREIGN KEY (buyer_id) REFERENCES users(id),
    CONSTRAINT fk_orders_seller FOREIGN KEY (seller_id) REFERENCES users(id),
    CONSTRAINT chk_orders_total CHECK (total_amount = final_price + shipping_fee)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Bảng ví tiền (wallets)
CREATE TABLE wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL UNIQUE,
    wallet_type VARCHAR(30) NOT NULL DEFAULT 'USER',
    available_balance BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_wallets_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_wallets_balance CHECK (available_balance >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Bảng nhật ký giao dịch tiền tệ (transactions)
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    amount BIGINT NOT NULL,
    balance_before BIGINT NOT NULL,
    balance_after BIGINT NOT NULL,
    reference_type VARCHAR(50) NULL,
    reference_id BIGINT NULL,
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_transactions_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Bảng đánh giá uy tín đơn hàng (reviews)
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    reviewee_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_reviewee FOREIGN KEY (reviewee_id) REFERENCES users(id),
    CONSTRAINT uq_reviews_order_reviewer UNIQUE (order_id, reviewer_id),
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Bảng thông báo người dùng (notifications)
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_type VARCHAR(30) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    action_url VARCHAR(500),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Bảng khiếu nại (complaints)
CREATE TABLE complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    complainant_id BIGINT NOT NULL,
    complaint_type VARCHAR(50) NOT NULL,
    reason TEXT NOT NULL,
    evidence_url VARCHAR(500),
    resolution_note TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    resolved_at DATETIME(3) NULL,
    CONSTRAINT fk_complaints_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_complaints_complainant FOREIGN KEY (complainant_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. Bảng cấu hình hệ thống (system_configs)
CREATE TABLE system_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value VARCHAR(500) NOT NULL,
    description TEXT,
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dữ liệu cấu hình ban đầu
INSERT INTO system_configs (config_key, config_value, description) VALUES
('platform_fee_percent', '5', 'Tỷ lệ phí hoa hồng sàn (5%)'),
('payment_deadline_hours', '72', 'Thời hạn thanh toán đơn hàng sau khi trúng đấu giá (72 giờ)'),
('max_unpaid_strikes', '3', 'Số gậy bùng hàng tối đa trước khi bị cấm đấu giá');
