-- =============================================================================
-- V1__init.sql - Khởi tạo toàn bộ cấu trúc Cơ sở dữ liệu Hệ Thống Đấu Giá
-- Khớp 100% với tài liệu docs/ERD.md (Ngày 1)
-- =============================================================================

-- 1. Bảng người dùng (users)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    citizen_id VARCHAR(20) UNIQUE,
    phone_number VARCHAR(20),
    bank_name VARCHAR(100),
    bank_account_number VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Bảng hồ sơ người bán (seller_profiles)
CREATE TABLE seller_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    profile_info TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    rejected_reason TEXT,
    reviewed_by BIGINT,
    reviewed_at DATETIME(3),
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_seller_profiles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_seller_profiles_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Bảng tài sản đấu giá (assets)
CREATE TABLE assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    asset_type VARCHAR(30) NOT NULL,
    main_image_url VARCHAR(500),
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_assets_seller FOREIGN KEY (seller_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Bảng hình ảnh phụ của tài sản (asset_images)
CREATE TABLE asset_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_asset_images_asset FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Bảng phiên đấu giá (auctions)
CREATE TABLE auctions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_id BIGINT NOT NULL UNIQUE,
    method VARCHAR(30) NOT NULL,
    starting_price BIGINT NOT NULL,
    step_price BIGINT,
    price_reduction_interval_seconds INT,
    floor_price BIGINT,
    deposit_percent DECIMAL(5, 2) NOT NULL,
    deposit_amount BIGINT NOT NULL,
    terms_and_conditions TEXT,
    listing_at DATETIME(3),
    registration_deadline DATETIME(3),
    start_time DATETIME(3) NOT NULL,
    end_time DATETIME(3) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_APPROVAL',
    current_price BIGINT,
    highest_bidder_id BIGINT,
    rejection_reason TEXT,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_auctions_asset FOREIGN KEY (asset_id) REFERENCES assets(id),
    CONSTRAINT fk_auctions_highest_bidder FOREIGN KEY (highest_bidder_id) REFERENCES users(id),
    CONSTRAINT chk_starting_price CHECK (starting_price >= 0),
    CONSTRAINT chk_deposit_percent CHECK (deposit_percent >= 5.00 AND deposit_percent <= 20.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Bảng đăng ký tham gia (registrations)
CREATE TABLE registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    bidder_id BIGINT NOT NULL,
    anonymous_code VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_APPROVAL',
    rejection_reason TEXT,
    terms_committed_at DATETIME(3) NOT NULL,
    decision_on_listing_change VARCHAR(20) DEFAULT 'CONTINUE',
    registered_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_registrations_auction FOREIGN KEY (auction_id) REFERENCES auctions(id),
    CONSTRAINT fk_registrations_bidder FOREIGN KEY (bidder_id) REFERENCES users(id),
    CONSTRAINT uq_reg_auction_bidder UNIQUE (auction_id, bidder_id),
    CONSTRAINT uq_reg_auction_code UNIQUE (auction_id, anonymous_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Bảng tiền đặt trước (deposits)
CREATE TABLE deposits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_id BIGINT NOT NULL UNIQUE,
    amount BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'HELD',
    refund_deadline DATETIME(3),
    transfer_deadline DATETIME(3),
    refunded_at DATETIME(3),
    transferred_to_seller_at DATETIME(3),
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_deposits_registration FOREIGN KEY (registration_id) REFERENCES registrations(id),
    CONSTRAINT chk_deposit_amount CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Bảng lượt trả giá (bids - Bất biến, append-only)
CREATE TABLE bids (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    bidder_id BIGINT NOT NULL,
    amount BIGINT NOT NULL,
    bid_type VARCHAR(20) NOT NULL,
    server_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    is_valid BOOLEAN NOT NULL DEFAULT TRUE,
    invalid_reason VARCHAR(255),
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_bids_auction FOREIGN KEY (auction_id) REFERENCES auctions(id),
    CONSTRAINT fk_bids_bidder FOREIGN KEY (bidder_id) REFERENCES users(id),
    CONSTRAINT chk_bid_amount CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Bảng giải quyết thanh toán (settlements)
CREATE TABLE settlements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    bidder_id BIGINT NOT NULL,
    winning_price BIGINT NOT NULL,
    deposit_deducted BIGINT NOT NULL,
    remaining_amount BIGINT NOT NULL,
    payment_deadline DATETIME(3) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'WAITING_FOR_PAYMENT',
    paid_at DATETIME(3),
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_settlements_auction FOREIGN KEY (auction_id) REFERENCES auctions(id),
    CONSTRAINT fk_settlements_bidder FOREIGN KEY (bidder_id) REFERENCES users(id),
    CONSTRAINT chk_winning_price CHECK (winning_price >= 0),
    CONSTRAINT chk_remaining_amount CHECK (remaining_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Bảng ví tiền (wallets)
CREATE TABLE wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    wallet_type VARCHAR(20) NOT NULL DEFAULT 'USER',
    available_balance BIGINT NOT NULL DEFAULT 0,
    frozen_balance BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_wallets_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_wallet_available CHECK (available_balance >= 0),
    CONSTRAINT chk_wallet_frozen CHECK (frozen_balance >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Bảng lịch sử giao dịch tiền (transactions - Bất biến, append-only)
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    amount BIGINT NOT NULL,
    balance_before BIGINT NOT NULL,
    balance_after BIGINT NOT NULL,
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    reference_type VARCHAR(50),
    reference_id BIGINT,
    description VARCHAR(255),
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_transactions_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Bảng thông báo người dùng (notifications)
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    target_url VARCHAR(255),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Bảng nhật ký kiểm toán (audit_logs - Bất biến, append-only)
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    action VARCHAR(50) NOT NULL,
    performed_by BIGINT,
    data_before JSON,
    data_after JSON,
    ip_address VARCHAR(45),
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_audit_logs_performer FOREIGN KEY (performed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. Bảng khiếu nại (complaints)
CREATE TABLE complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    evidence_url VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',
    resolution_note TEXT,
    resolved_by BIGINT,
    resolved_at DATETIME(3),
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_complaints_auction FOREIGN KEY (auction_id) REFERENCES auctions(id),
    CONSTRAINT fk_complaints_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_complaints_resolver FOREIGN KEY (resolved_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. Bảng cấu hình hệ thống (system_configs)
CREATE TABLE system_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. Bảng ngày nghỉ lễ (holidays - Bảng ngoài SRS phục vụ BusinessCalendar)
CREATE TABLE holidays (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    holiday_date DATE NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. Bảng theo dõi phiên yêu thích (auction_follows - FR-US-04)
CREATE TABLE auction_follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    auction_id BIGINT NOT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_auction_follows_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_auction_follows_auction FOREIGN KEY (auction_id) REFERENCES auctions(id),
    CONSTRAINT uq_user_auction_follow UNIQUE (user_id, auction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. Bảng biên bản điện tử (auction_minutes - BR-32)
CREATE TABLE auction_minutes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL UNIQUE,
    snapshot_data JSON NOT NULL,
    generated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_auction_minutes_auction FOREIGN KEY (auction_id) REFERENCES auctions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 19. Bảng yêu cầu thay đổi niêm yết (listing_change_requests - UC-07)
CREATE TABLE listing_change_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    change_summary TEXT NOT NULL,
    old_values JSON NOT NULL,
    new_values JSON NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    reviewed_by BIGINT,
    reviewed_at DATETIME(3),
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_listing_change_auction FOREIGN KEY (auction_id) REFERENCES auctions(id),
    CONSTRAINT fk_listing_change_seller FOREIGN KEY (seller_id) REFERENCES users(id),
    CONSTRAINT fk_listing_change_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- SEED DỮ LIỆU BAN ĐẦU CỐT LÕI
-- =============================================================================

-- Khởi tạo 2 ví hệ thống độc lập theo BR-10 & BR-29 (user_id = NULL)
INSERT INTO wallets (user_id, wallet_type, available_balance, frozen_balance) 
VALUES 
    (NULL, 'SYSTEM_ESCROW', 0, 0),
    (NULL, 'SYSTEM_REVENUE', 0, 0);

-- Khởi tạo các tham số cấu hình mặc định (FR-AD-07)
INSERT INTO system_configs (config_key, config_value, description) 
VALUES 
    ('SERVICE_FEE_PERCENT', '5.0', 'Tỷ lệ phí dịch vụ thu từ người trúng đấu giá (%)'),
    ('MIN_DEPOSIT_PERCENT', '5.0', 'Tỷ lệ đặt trước tối thiểu (%)'),
    ('MAX_DEPOSIT_PERCENT', '20.0', 'Tỷ lệ đặt trước tối đa (%)'),
    ('PAYMENT_DEADLINE_MINUTES', '15', 'Thời hạn thanh toán sau khi trúng đấu giá (phút)'),
    ('COMPLAINT_WINDOW_DAYS', '30', 'Thời hạn cho phép gửi khiếu nại sau khi phiên kết thúc (ngày)'),
    ('COMPLAINT_RESOLUTION_DAYS', '7', 'Thời hạn Admin phải phản hồi khiếu nại (ngày)');

-- Khởi tạo tài khoản Quản trị viên (Admin) mặc định
-- Mật khẩu mặc định: admin123 (BCrypt hash)
INSERT INTO users (username, email, password_hash, full_name, role, status)
VALUES 
    ('admin', 'admin@auction.vn', '$2a$10$w3P4Bq28n3h5nK79Q.W.peB5ZgQ7H8mYj2Ie4C9iFpP.oN.9jT16q', 'Quản Trị Viên Hệ Thống', 'ADMIN', 'ACTIVE');
